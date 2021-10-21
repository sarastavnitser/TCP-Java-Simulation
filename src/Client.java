import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Client {

    public static void main(String[] args) {
        System.out.println("Requesting message from server");
        if (args.length < 2) {
            System.out.println("no arguments ");
        } else {
            String hostName = args[0];
            int portNumber = Integer.parseInt(args[1]);

            try {
                /*
                 * initialize socket
                 * initialize input and output streams*/
                Socket socket = new Socket("localhost", 6666);
                InputStream input = socket.getInputStream();
                DataInputStream dataInput = new DataInputStream(input);
                OutputStream output = socket.getOutputStream();
                DataOutputStream outputStream = new DataOutputStream(output);
                //BufferedReader bufferMessage = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                /*
                 * initialize array lists for received packets*/
                ArrayList<String> receivedChar = new ArrayList<>();
                ArrayList<Integer> notReceivedIndex = new ArrayList<Integer>();
                ArrayList<Integer> receivedIndex = new ArrayList<Integer>();

                Integer total = 0;
                String finalMess = "Final Message sent";

                do {
                    do {
                        notReceivedIndex.clear();
                        String thisPacket = dataInput.readUTF();

                        if (thisPacket.equals(finalMess)) {
                            System.out.println("Final message received");
                            break;
                        } else {
                            /*
                             * read in the packets that are being received*/
                            String firstPacket = thisPacket;
                            //reading in the index of the letter
                            String firstIndexStr = firstPacket.split(" ")[0];
                            Integer firstIndex = Integer.parseInt(firstIndexStr);
                            receivedIndex.add(firstIndex);
                            firstPacket = firstPacket.replace(firstIndexStr, "").strip();
                            //reading in the total
                            String totalString = firstPacket.split(" ")[0];
                            total = Integer.parseInt(totalString);
                            firstPacket = firstPacket.replace(totalString, "").strip();
                            //reading in the letter of the message
                            String message = firstPacket;
                            receivedChar.add(message);

                            System.out.println("Adding message " + message + " Packet number " + firstIndex + " out of " + total);
                        }


                    } while (dataInput.available() > 0);

                    /*
                     * clear the notReceived array for next time*/


                    /*
                     * add not received indexes to array*/
                    for (int i = 0; i < total; i++) {
                        if (!receivedIndex.contains(i)) {
                            notReceivedIndex.add(i);
                        }
                    }


                    /*
                     * request not received packets from server
                     */
                    for (int i : notReceivedIndex) {
                        System.out.println("Requesting package " + i);
                        outputStream.writeInt(i);
                    }


                } while (receivedIndex.size() != total);

                /*
                 * if all packets are received then print message*/
                System.out.println("\nAll packages received");
                ArrayList<String> finalMessage = new ArrayList<>();
                for (int i = 0; i < receivedIndex.size(); i++) {

                    finalMessage.add(receivedChar.get(receivedIndex.indexOf(i)));
                }

                //putting message together
                StringBuilder mess = new StringBuilder();
                for (int i = 0; i < finalMessage.size(); i++) {
                    if (finalMessage.get(i).equals("")) {
                        mess.append(" ");
                    }
                    mess.append(finalMessage.get(i));
                }

                System.out.println("\nMessage: " + mess.toString());
                outputStream.writeInt(-1);

                dataInput.close();
                outputStream.close();
                socket.close();

            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " + hostName);
                e.printStackTrace();
                System.exit(1);
            }

        }
    }

}