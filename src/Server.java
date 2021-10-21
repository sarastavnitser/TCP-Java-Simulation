import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("no port number entered ");
        } else {
            int portNumber = Integer.parseInt(args[1]);


            try {
                ServerSocket serverSocket = new ServerSocket(6666);

                System.out.println("Server waiting for client request ");

                /*
                 * initialize sockets
                 * initialize input streams and output streams
                 * initialized buffer reader
                 * */
                Socket clientSocket = serverSocket.accept();
                InputStream input = clientSocket.getInputStream();
                DataInputStream dataInput = new DataInputStream(input);
                OutputStream output = clientSocket.getOutputStream();
                DataOutputStream outputStream = new DataOutputStream(output);
                BufferedReader bufferMessage = new BufferedReader(new InputStreamReader(System.in));
                String usersMessage = bufferMessage.readLine();

                /*
                 * set total equal based on the users message length */
                final Integer TOTAL = usersMessage.length();

                /*
                 * create an array list of indexes for the message*/
                ArrayList<Integer> messageIndex = new ArrayList<>();


                /*
                 * added the characters to the users message array list */
                for (int i = 0; i < usersMessage.length(); i++) {
                    messageIndex.add(i);
                }

                /*
                 * shuffled the array of indexes*/
                Collections.shuffle(messageIndex);


                String finalMess = "Final Message sent";
                /*
                 * Writing which messages are being sent*/
                while (messageIndex.size() > 0) {
                    System.out.println("\nServer waiting for client request ");

                    for (int i = 0; i < messageIndex.size() - 1; i++) {
                        System.out.println("Sending packet: " + messageIndex.get(i) + " out of " + TOTAL + " Message: " + usersMessage.charAt(messageIndex.get(i)));
                        int rand = (int) (100 * Math.random());

                        /*
                         * only sending with 80% probability
                         * */
                        if (rand < 80) {
                            StringBuilder str = new StringBuilder();
                            //sending the index of where the letter is
                            str.append(messageIndex.get(i)).append(" ");
                            str.append(TOTAL).append(" ");
                            //sending the letter of the message
                            str.append(usersMessage.charAt(messageIndex.get(i))).append(" ");
                            outputStream.writeUTF(String.valueOf(str));
                            outputStream.flush();

                        }
                    }
                    /*
                     * writing that last packet was sent and making sure it was for sure sent*/
                    StringBuilder str = new StringBuilder();
                    //sending index of the letter
                    str.append(messageIndex.get(messageIndex.size() - 1)).append(" ");
                    str.append(TOTAL).append(" ");
                    //sending letter of the message
                    str.append(usersMessage.charAt(messageIndex.get(messageIndex.size() - 1))).append(" ");
                    outputStream.writeUTF(String.valueOf(str));
                    outputStream.flush();
                    System.out.println("Sending packet: " + messageIndex.get(messageIndex.size() - 1) + " out of " + TOTAL + " Message: " + usersMessage.charAt(messageIndex.get(messageIndex.size() - 1)));
                    outputStream.writeUTF(finalMess);
                    outputStream.flush();
                    System.out.println(finalMess);
                    /*
                     * create array list to see what was received by the client*/
                    ArrayList<Integer> notReceived = new ArrayList<>();
                    do {
                        notReceived.add(dataInput.readInt());
                    }
                    while (dataInput.available() > 0);

                    if (notReceived.get(0) == -1) {

                        System.out.println("\nAll packets sent");

                        dataInput.close();
                        outputStream.close();
                        clientSocket.close();
                        break;
                    }
                    for (int i = 0; i < notReceived.size(); i++) {
                        System.out.println("Not received " + notReceived.get(i));
                    }

                    /*
                     * remove received packets from message index array*/
                    int i;
                    ArrayList<Integer> received = new ArrayList<>();
                    for (i = 0; i < messageIndex.size(); i++) {
                        if (!notReceived.contains(messageIndex.get(i))) {
                            received.add(messageIndex.get(i));

                        }
                    }
                    messageIndex.removeAll(received);
                    received.clear();

                    notReceived.clear();

                }


            } catch (
                    IOException e) {
                e.printStackTrace();
                System.out.println(
                        "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
                System.out.println(e.getMessage());
            }

        }
    }
}