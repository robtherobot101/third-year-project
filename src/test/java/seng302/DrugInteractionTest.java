package seng302;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import seng302.Core.DrugInteraction;
import seng302.Core.Main;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DrugInteractionTest {
    private static DrugInteraction drugInteraction;

    @Before
    public void setUp() {
        try{
            String json = new String(Files.readAllBytes(Paths.get("src/test/java/seng302/DrugInteractionTestingJson")));
            drugInteraction = new DrugInteraction(json);
        }catch(IOException e) {
            System.out.println(e);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @After
    public void tearDown(){

    }

    @Test
    public void ageInteraction_negativeAge_returnsEmptyHashSet(){
        System.out.println(drugInteraction.ageInteraction(-1));
    }
}