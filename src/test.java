import mpi.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.InputMismatchException;
import java.util.Scanner;

public class test {

    static String directory = "";
    static String fileName = "";
    static String[] input = {"",""};
    static float[][] kernel;
    static int endY;

    public static void main(String[] args) throws Exception {

        // start routine
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


        // becchemo sta image
        BufferedImage image = ImageIO.read(new File(directory + fileName));
        int width = image.getWidth();
        int height = image.getHeight();

        // there can't be non-MPI stuff between Init & Finalize
        MPI.Init(args);
        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        if (rank == 0) { // MASTER PROCESS: divide into strips, compute own strip, send strips to workers, receive strips from workers & merge back together.

            // divide image into strips
            int stripHeight =  height / size;
            for (int i = 0; i < size; i++) {
                int startY = i * stripHeight;
                if (i == size - 1) endY = height;
                else {endY = startY + stripHeight;}
                BufferedImage strip = image.getSubimage(0, startY, width, endY - startY);

                sendStrip(strip, i);
            }

            // send strip to workers

            // compute own strip

            // receive strip from workers

            // merge back together

        } else {    // WORKER PROCESS: receive strip, compute strip, send strip back to master

            // receive strip

            // compute strip

            // receive strip

        }


        MPI.Finalize();
    }



    //public static returnStrip(){}

    //public static receiveStrip(){}

    public static BufferedImage applyKernel(BufferedImage image, float[][] kernel ){
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float sumRed = 0;
                float sumGreen = 0;
                float sumBlue = 0;

                // applying kernel loop
                for (int i = -1; i <= 1 ; i++) {
                    for (int j = -1; j <= 1; j++) {
                        Color pixelColor = new Color(image.getRGB(x + j, y + i));
                        sumRed += pixelColor.getRed() * kernel[i+1][j+1];
                        sumGreen += pixelColor.getGreen() * kernel[i+1][j+1];
                        sumBlue += pixelColor.getBlue() * kernel[i+1][j+1];
                    }
                }
                int red = (int) Math.min(255, Math.max(0,sumRed));
                int green = (int) Math.min(255, Math.max(0,sumGreen));
                int blue = (int) Math.min(255, Math.max(0,sumBlue));
                int rgb = new Color(red, green, blue).getRGB();
                output.setRGB(x, y, rgb);
            }
        }
        return output;
    }

    public static void sendStrip(BufferedImage strip, int dest_rank) throws MPIException{

        int height = strip.getHeight();
        int width = strip.getWidth();
        int[] stripx = strip.getRGB(0,0, width, height, null, 0, width);
        // we are sending: buffer, 0: buffer sarting point, 2: no of elm to send, type of data, destination rank, id of message
        MPI.COMM_WORLD.Send(new int[]{width, height}, 0, 2, MPI.INT, dest_rank, 0);
        // sending: rgb values, 0: buffer starting pint, w*h is total no of elm, type int, dest rank, id of msg
        MPI.COMM_WORLD.Send(stripx, 0, width * height, MPI.INT, dest_rank, 1);

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
