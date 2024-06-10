import mpi.*;

import java.awt.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class test {

    static String directory = "";
    static String fileName = "";
    static String[] input = {"",""};
    static float[][] kernel;

    public static void main(String[] argv) throws Exception {

        welcomeMessage();
        kernel = getKernel();
        input = getImage();
        System.out.println(input[0] + input[1]);
        directory = input[0];
        fileName = input[1];

        //troubleshoot
        System.out.println("Selected kernel:");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                float value = (kernel[i][j]);
                System.out.print(value + " ");
            }
            System.out.println();
        }

        //MPI.Init(argv);
        //int rank = MPI.COMM_WORLD.Rank();
        //int size = MPI.COMM_WORLD.Size();

    }

    public static String[] getImage(){

        String[] input = {"",""};
                FileDialog fileDialog = new FileDialog((Frame) null, "Select an Image");
                fileDialog.setVisible(true);

                // get directory and file name
                input[0] = fileDialog.getDirectory();
                input[1] = fileDialog.getFile();

                // If a file was selected
                if (fileName != null) {
                    // Process the selected file
                    System.out.println("Selected file: " + directory + fileName);
                }
        return input;
    }

    public static float[][] getKernel() {
        Scanner scan = new Scanner(System.in);
        int selectedKernel = 0;

        float[][] kernel = new float[][]{ // DEFAULT KERNEL IS IDENTITY
                {0, 0, 0},
                {0, 1, 0},
                {0, 0, 0}
        };

        System.out.println("Select kernel: \n[1] identity\n[2] sharpen\n[3] box blur\n[4] gaussian blur\n[5] edge detection\ninput: ");
        try {
            selectedKernel = scan.nextInt();
            //System.out.println("Selected kernel: " + selectedKernel);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid integer.");
            scan.nextLine();
            getKernel();
        }


        switch (selectedKernel) {
            case 1:
                System.out.println("Selected kernel: Identity");
                kernel = new float[][]{
                        {0, 0, 0},
                        {0, 1, 0},
                        {0, 0, 0}
                };
                break;
            case 2:
                System.out.println("Selected kernel: Sharpen");
                kernel = new float[][] {
                        { 0, -1, 0},
                        {-1, 5, -1},
                        { 0, -1, 0}
                };
                break;
            case 3:
                System.out.println("Selected kernel: Box blur");
                kernel = new float[][] {
                        {1,1,1},
                        {1,1,1},
                        {1,1,1}
                };
                for (int i = 0; i < kernel.length; i++) {
                    for (int j = 0; j < kernel[i].length; j++) kernel[i][j] = kernel[i][j] * (float)(1.0/9);
                }
                break;
            case 4:
                System.out.println("Selected kernel: Gaussian blur");
                kernel = new float[][] {
                        {1, 2, 1},
                        {2, 4, 2},
                        {1, 2, 1}
                };
                for (int i = 0; i < kernel.length; i++) {
                    for (int j = 0; j < kernel[i].length; j++) kernel[i][j] = kernel[i][j] * (float)(1.0/16);
                }
                break;
            case 5:
                System.out.println("Selected kernel: Edge detection");
                kernel = new float[][] {
                        {-1, -1, -1},
                        {-1,  8, -1},
                        {-1, -1, -1}
                };
                break;
        }
        return kernel;
    }

    public static void welcomeMessage(){
        System.out.println(
                """
                        ┌──────────────────────────────────┐
                        │ Kernel image processing program. │
                        └──────────────────────────────────┘
                        > Select kernel you want to use \s
                        > Select image you want to apply\s
                          kernel to.
                """);
    }
}
