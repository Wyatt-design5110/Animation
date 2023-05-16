import java.util.Random;

public class ArrayGen { Random rd = new Random();

    public int[] RandomIntArray(int arrayLength, int max) {
        int[] a = new int[arrayLength]; for(int i=0; i<a.length; i++) {
            a[i] = rd.nextInt(max); }
        return a; }
}