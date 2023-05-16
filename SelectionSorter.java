import java.awt.BorderLayout;
import javax.swing.*;
import java.util.concurrent.locks.ReentrantLock;
import java.awt.*;
import java.awt.geom.Line2D;
import java.util.*;


public class SelectionSorter {
    public SelectionSorter(int[] anArray, JComponent aComponent) {
        a = anArray;
        sortStateLock = new ReentrantLock();
        component = aComponent;
    }

    public void pause(int steps) throws InterruptedException {
        component.repaint();
        Thread.sleep(steps * DELAY);
    }

    public void sort() throws InterruptedException {
        int out, min;
        for (out = 0; out < a.length; out++) {
            min = minimumPosition(out);
            swap(out, min);
            alreadySorted = out;
        }
    }

    public int minimumPosition(int from) throws InterruptedException {
        int minPos = from;
        for (int i = from + 1; i < a.length; i++) {
            sortStateLock.lock();
            try {
                if (a[i] < a[minPos])
                    minPos = i;
                markedPosition = i;
            }
            finally {
                sortStateLock.unlock();
            }
            pause(2);
        }
        return minPos;
    }
    private void swap(int first, int second) {
        int temp = a[first];
        a[first] = a[second];
        a[second] = temp;
    }

    public void draw(Graphics2D g2) {
        sortStateLock.lock();
        try {
            int deltaX = component.getWidth() / a.length;
            for (int i = 0; i < a.length; i++) {
                if (i == markedPosition)
                    g2.setColor(Color.RED);
                else if (i <= alreadySorted)
                    g2.setColor(Color.BLUE);
                else
                    g2.setColor(Color.BLACK);
                g2.draw(new Line2D.Double(i * deltaX, 0, i * deltaX, a[i]));
            }
        }
        finally {
            sortStateLock.unlock();
        }
    }
    private int[] a;
    private ReentrantLock sortStateLock;
    private JComponent component;
    private int markedPosition = -1;
    private int alreadySorted = -1;
    private final int DELAY = 10;
}
class SelectionSortComponent extends JComponent {
    private SelectionSorter sorter;

    public void paintComponent(Graphics g) {
        if (sorter == null)
            return;
        Graphics2D g2 = (Graphics2D) g;
        sorter.draw(g2);
    }
    public void startAnimation() {
        int[] values = randomIntArray(40, 500);
        sorter = new SelectionSorter(values, this);

        class AnimationRunnable implements Runnable {
            public void run() {
                try {
                    sorter.sort();
                }
                catch(InterruptedException exception) {

                }
            }
        }
        Runnable r = new AnimationRunnable();
        Thread t = new Thread(r);
        t.start();
    }

    public int[] randomIntArray(int length, int size) {
        Random r = new Random();
        int[] numbers = new int[length];
        for(int i = 0; i < length; i++) {
            numbers[i] = r.nextInt(size + 1);
        }
        return numbers;
    }
}
class SelectionSortViewer {
    public static void main(String[] args) throws Exception {
        JFrame frame = new JFrame();
        frame.setSize(600, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SelectionSortComponent animation = new SelectionSortComponent();
        frame.add(animation, BorderLayout.CENTER);

        frame.setVisible(true);
        animation.startAnimation();
    }
}