package com.shodo.io;

import com.shodo.io.exceptions.ScoresEngineInputException;
import com.shodo.io.scoring.InputFilesService;
import com.shodo.io.scoring.RankService;
import com.shodo.io.scoring.ScoresService;
import com.shodo.io.scoring.UserPromptService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ScoresMainApp {

    private static ScoresService scoresService;
    private static UserPromptService userPromptService;
    private static InputFilesService inputFilesService;
    private static RankService rankService;

    /**
     * Search Engine main function. It takes the words given on the prompt and return
     * a list of the top 10 matching filenames in rank order.
     * <p>
     *     <ul>
     *          <li>The rank score must be 100% if a file contains all the words</li>
     *          <li>It must	be 0% if it contains none of the words</li>
     *          <li>It should be between 0 and 100 if it contains only some of the words</li>
     *     </ul>
     * </p>
     *
     * @param args Program arguments containing the directory in which research is done.
     */
    public static void main(String[] args) throws ScoresEngineInputException {
        userPromptService = new UserPromptService();
        inputFilesService = new InputFilesService();
        rankService = new RankService();
        scoresService = new ScoresService(userPromptService, rankService);
        if (args.length == 0) {
            throw new ScoresEngineInputException("No directory given to index. Please type directory containing files to read.");
        }
        try {
            var directory = args[0];
            inputFilesService.displayFilesCount(directory, "txt");
            readFilesAndShowScores(directory);
        } catch (IOException e) {
            System.err.println("The program can't find the specified folder. Please type an existing one.");
        }
    }

    /**
     * Gneral algorithm calculating the scores.
     * <p>Note : 'exit' is a reserved word but alone. If typed alone, the program will exit.</p>
     *
     * @param directory The directory containing files
     * @throws IOException If the directory is not read.
     */
    private static void readFilesAndShowScores(String directory) throws IOException {
        try (var filesPaths = Files.list(Paths.get(directory))) {
            var filesContent = inputFilesService.readFilesContent(filesPaths);
            while (true) {
                if (scoresService.calculateScores(filesContent)) break;
            }
        }
    }

}
