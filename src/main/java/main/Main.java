package main;
import testsmell.AbstractSmell;
import testsmell.ResultsWriter;
import testsmell.TestFile;
import testsmell.TestSmellDetector;
import testsmell.smell.AssertionRoulette;
import testsmell.smell.EagerTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "TSDectect-HTML", defaultPhase = LifecyclePhase.TEST)
public class Main extends AbstractMojo {
	
	@Parameter(property = "path", required = true)
	private String path;
	
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (path == null) {
            System.out.println("Please provide the file containing the paths to the collection of test files");
            return;
        }
        if(!path.isEmpty()){
            File inputFile = new File(path);
            if(!inputFile.exists() || inputFile.isDirectory()) {
                System.out.println("Please provide a valid file containing the paths to the collection of test files");
                return;
            }
        }

        TestSmellDetector testSmellDetector = new TestSmellDetector();

        /*
          Read the input file and build the TestFile objects
         */
        BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String str;

        String[] lineItem;
        TestFile testFile;
        List<TestFile> testFiles = new ArrayList<>();
        try {
			while ((str = in.readLine()) != null) {
			    // use comma as separator
			    lineItem = str.split(",");

			    //check if the test file has an associated production file
			    if(lineItem.length ==2){
			        testFile = new TestFile(lineItem[0], lineItem[1], "");
			    }
			    else{
			        testFile = new TestFile(lineItem[0], lineItem[1], lineItem[2]);
			    }

			    testFiles.add(testFile);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        /*
          Initialize the output file - Create the output file and add the column names
         */
        ResultsWriter resultsWriter = null;
		try {
			resultsWriter = ResultsWriter.createResultsWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        List<String> columnNames;
        List<String> columnValues;

        columnNames = testSmellDetector.getTestSmellNames();
        columnNames.add(0, "App");
        columnNames.add(1, "TestClass");
        columnNames.add(2, "TestFilePath");
        columnNames.add(3, "ProductionFilePath");
        columnNames.add(4, "RelativeTestFilePath");
        columnNames.add(5, "RelativeProductionFilePath");

        try {
			resultsWriter.writeColumnName(columnNames);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        /*
          Iterate through all test files to detect smells and then write the output
        */
        TestFile tempFile = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date;
        for (TestFile file : testFiles) {
            date = new Date();
            System.out.println(dateFormat.format(date) + " Processing: "+file.getTestFilePath());
            System.out.println("Processing: "+file.getTestFilePath());

            //detect smells
            try {
				tempFile = testSmellDetector.detectSmells(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

            //write output
            columnValues = new ArrayList<>();
            columnValues.add(file.getApp());
            columnValues.add(file.getTestFileName());
            columnValues.add(file.getTestFilePath());
            columnValues.add(file.getProductionFilePath());
            columnValues.add(file.getRelativeTestFilePath());
            columnValues.add(file.getRelativeProductionFilePath());
            for (AbstractSmell smell : tempFile.getTestSmells()) {
                try {
                    columnValues.add(String.valueOf(smell.getHasSmell()));
                }
                catch (NullPointerException e){
                    columnValues.add("");
                }
            }
            try {
				resultsWriter.writeLine(columnValues);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        System.out.println("end");
    }


}
