package UI.Support;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * Tiny helper around {@link SwingWorker} so callers don't have to repeat the
 * try/catch in done(). Use it like:
 *
 *   AsyncTask.run(parent,
 *       () -> service.getQuizzes(),
 *       quizzes -> render(quizzes),
 *       "Failed to load quizzes");
 */
public final class AsyncTask {

    private AsyncTask() {}

    public interface Producer<T> { T call() throws Exception; }

    public static <T> void run(Component parent,
                               Producer<T> background,
                               Consumer<T> onSuccess,
                               String errorTitle) {
        new SwingWorker<T, Void>() {
            @Override protected T doInBackground() throws Exception {
                return background.call();
            }
            @Override protected void done() {
                try {
                    onSuccess.accept(get());
                } catch (Exception e) {
                    Throwable cause = e.getCause() != null ? e.getCause() : e;
                    cause.printStackTrace();
                    JOptionPane.showMessageDialog(parent,
                            errorTitle + ":\n" + cause.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }
}