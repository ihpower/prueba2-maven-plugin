package testsmell;

import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

/**
 * This class is utilized to write output to a CSV file
 */
public class ResultsWriter {

    private String outputFile;
    private FileWriter writer;

    /**
     * Creates the file into which output it to be written into. Results from each file will be stored in a new file
     * @throws IOException
     */
    private ResultsWriter() throws IOException {
        String time =  String.valueOf(Calendar.getInstance().getTimeInMillis());
        outputFile = MessageFormat.format("{0}_{1}_{2}.{3}", "Output","TestSmellDetection",time, "html");
        writer = new FileWriter(outputFile,false);
        writer.write("<html><head><title>Test Smell Detection Results</title></head><body><table border=\"1\">");
    }

    /**
     * Factory method that provides a new instance of the ResultsWriter
     * @return new ResultsWriter instance
     * @throws IOException
     */
    public static ResultsWriter createResultsWriter() throws IOException {
        return new ResultsWriter();
    }

    /**
     * Writes column names into the CSV file
     * @param columnNames the column names
     * @throws IOException
     */
    public void writeColumnName(List<String> columnNames) throws IOException {
    	writeOutput("<tr>", columnNames, "</tr>");
    }

    /**
     * Writes column values into the CSV file
     * @param columnValues the column values
     * @throws IOException
     */
    public void writeLine(List<String> columnValues) throws IOException {
    	writeOutput("<tr>", columnValues, "</tr>");
    }

    /**
     * Appends the input values into the CSV file
     * @param dataValues the data that needs to be written into the file
     * @throws IOException
     */
    private void writeOutput(String startTag, List<String> dataValues, String endTag) throws IOException {
        writer = new FileWriter(outputFile, true);

        writer.append(startTag);
        for (int i = 0; i < dataValues.size(); i++) {
            writer.append("<td>").append(String.valueOf(dataValues.get(i))).append("</td>");
        }
        writer.append(endTag).append(System.lineSeparator());

        writer.flush();
        writer.close();
    }
    
    public void close() throws IOException {
        writer = new FileWriter(outputFile, true);
        writer.write("</table></body></html>");
        writer.close();
    }
}
