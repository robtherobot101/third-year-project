package seng302.Logic;

public class CommandLineResponse {
    private boolean success;
    private String response;
    public CommandLineResponse(boolean success, String response){
        this.success = success;
        this.response = response;
    }

    public boolean isSuccess(){
        return success;
    }

    public String getResponse(){
        return response;
    }
}
