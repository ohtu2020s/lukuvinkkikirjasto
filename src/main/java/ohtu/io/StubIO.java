package ohtu.io;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.Condition;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.function.Function;

import org.javatuples.Pair;

/**
 * Input and output implementation with support for controlling from an another thread.
 *
 * <h1>Method of operation</h1>
 * The core of this implementation consists of three queues:
 *
 * <ol>
 *  <li>
 *    {@link #inChannel}: When the UI requires user input, it blocks by polling this queue.
 *    The controlling thread can detect when the UI is blocking on user input and add input
 *    to this queue if available. A transfer queue is not really a proper "queue" as it has
 *    maximum size of 1.
 *  </li>
 *  <li>
 *    {@link #outChannel}: Dynamically sized queue into which the UI appends all printed output.
 *    Unlike {@link #inChannel} this channel has unlimited size and the UI never blocks on output.
 *  </li>
 *  <li>
 *    {@link #inQueue}: Non-concurrent queue, which holds the lines of input before they are
 *    transfered to {@link #inChannel}. The order of elements in this queue can change or new
 *    lines can be inserted. The first line from this queue is only transfered to {@link #inChannel} 
 *    when the UI is blocking on input.
 *  </li>
 * </ol>
 *
 *  The queue for input is split into {@link #inChannel} and {@link #inQueue} because this
 *  implementation supports dynamically inserting lines of input based on output. Thus we
 *  need both the flexibility of {@link #inQueue} and the concurrency of {@link #inChannel}.
 */
public class StubIO implements IO {
    /**
     * Channel of input lines from the controlling thread to the UI thread.
     *
     * This channel has a maximum size of 1, and is used as an rendezvous point
     * between the threads. A {@link TransferQueue} is used over {@link SynchronousQueue}
     * because of it's {@link TransferQueue#hasWaitingConsumer} method.
     */
    private TransferQueue<String> inChannel = new LinkedTransferQueue<>();

    /**
     * Channel of output lines from the UI thread to the controlling thread.
     *
     * Dynamically sized channel with no upper size limit.
     */
    private BlockingQueue<String> outChannel = new LinkedBlockingQueue<>();

    /**
     * Queue of input lines to be handed off to the UI thread.
     *
     * Lines of input are held in this queue until the UI is ready to read
     * the next line of input, after which the next input line is transfered
     * to {@link #inChannel}. This allows us to change the content of the
     * input queue right up to the point when the UI reads the next line.
     */
    private ArrayDeque<String> inQueue = new ArrayDeque<>();

    /**
     * List of conditional inputs.
     *
     * Each pair consists of the output fragment required to trigger the input
     * and the input itself, in this order. Entries are removed from this
     * after they have been triggered.
     */
    private ArrayList<Pair<String, String>> triggers = new ArrayList<>();

    private volatile boolean inputClosed = false;
    private volatile boolean outputClosed = false;

    /**
     * Special value denoting that the default input value should be used
     * if one exists. Take note that the <i>content</i> of this constant
     * is not used as the special value, but instead the <i>instance</i>
     * itself, meaning that normal comparison operator should be used when
     * checking against this value, instead of the usual {@link String#equals}.
     */
    static String defaultInput = "<default>";

    /**
     * Append a line of input to the input queue.
     *
     * @param input A single line of input.
     */
    public void input(String input) {
        inQueue.addLast(input);
    }

    /**
     * Append a marker to the input queue, which denotes that the default
     * value of a prompt should be used.
     */
    public void input() {
        inQueue.addLast(defaultInput);
    }

    /**
     * Declare a piece of conditional input.
     *
     * The next time {@code trigger} is encountered in the output,
     * insert {@code input} to the <i>head</i> of the input queue.
     *
     * @param trigger Fragment of output which triggers the insertion of
     *   the given string into the input queue.
     * @param input The piece of input to be inserted into the input queue.
     */
    public void trigger(String trigger, String input) {
        if (input == null) {
            input = defaultInput;
        }

        triggers.add(Pair.with(trigger, input));
    }

    @Override
    public char nextChar() throws InterruptedException {
        return nextString().charAt(0);
    }

    @Override
    public String nextString() throws InterruptedException {
        if (inputClosed) {
            throw new InterruptedException("controlling side has closed input");
        }

        return inChannel.take();
    }

    @Override
    public void print(String text) {
        try {
            outChannel.put(text);
        } catch (InterruptedException ie) {
        }
    }

    @Override
    public String prompt(String prompt) throws InterruptedException {
        print(prompt);
        return nextString();
    }

    @Override
    public String prompt(String promptText, String buffer) throws InterruptedException {
        String line = prompt(promptText);

        if (line == defaultInput) {
            return buffer;
        } else {
            return line;
        }
    }

    /**
     * Method executed for each new line of output received from the UI.
     */
    private void handleOutput(String output) {
        for (int i = 0; i < triggers.size(); i++) {
            Pair<String, String> entry = triggers.get(i);
            String trigger = entry.getValue0();
            String input = entry.getValue1();

            if (output.contains(trigger)) {
                inQueue.addFirst(input);
                triggers.remove(i);
                i--;
            }
        }
    }

    /**
     * Processes output and feeds input to the UI until a line of output
     * matching the predicate is encountered.
     *
     * @param predicate Execution is halted when this function returns {@code true}.
     *
     * @return {@code true} if a line matching the predicate was encountered,
     * {@code false} if exeuction was terminated due to some other reason.
     */
    public boolean runUntil(Function<String, Boolean> predicate) {
        while (true) {
            String output = outChannel.poll();

            if (output != null) {
                handleOutput(output);

                if (predicate.apply(output)) {
                    return true;
                }

                continue;
            }

            if (outputClosed) {
                return false;
            }

            if (inChannel.hasWaitingConsumer()) {
                try {
                    String input = inQueue.pollFirst();

                    if (input != null) {
                        inChannel.transfer(input);
                    } else if (outChannel.isEmpty()) {
                        return false;
                    }
                } catch (InterruptedException ie) {
                    return false;
                }
            }
        }
    }

    /**
     * Processes output and feeds input to the UI until the input queue
     * is empty or the UI terminates due to some other reason.
     */
    public void run() {
        runUntil(out -> false);
    }

    /**
     * Marks the input stream as closed.
     * Causes any further read operations to throw an
     * {@code InterruptedException} instead of blocking.
     */
    public void closeInput() {
        inputClosed = true;
    }

    /**
     * Marks the output stream as closed.
     * Causes any operations waiting for futher output to terminate.
     */
    public void closeOutput() {
        outputClosed = true;
    }
}
