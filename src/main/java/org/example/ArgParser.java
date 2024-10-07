package org.example;

import java.util.Arrays;


/*
*   Ideally this will eventually be better at parsing input from user, right now I am just trying to make it work
*
* */
public class ArgParser {
//    Map<String,> argMap = new HashMap<>();


    // THIS IS GROSS CLEAN IT UP
    ParsedInput parse(String[] args)
    {
        ParsedInput parsedInput = new ParsedInput();
        boolean convertFlag = false;
        // Have list of flags and check if any are marked true, if so then the next argument is for that flag
        if(Arrays.stream(args).anyMatch(a -> a.equals("-h") || a.equals("-help")))
        {
            System.out.println("""
                    Welcome to Jawall! Java implementation of gowall and pywall.\
                    
                    Usage:
                        jawall [flags]
                        jawall [command]
                    
                     example use:\
                     jawall convert [filepath] -t [theme name]
                     jawall convert ~/Pictures/wallpapers/# -t rainbow
                     
                     in the above use, you are telling jawall to convert every image in your wallpapers
                     folder to your rainbow theme.
                     
                     flags:
                        -h or -help: prints out a help message
                        -t or -theme selects theme to be used for conversion
                       
                     commands:
                        convert: converts image or images to be specified
                        
                        more to come...
                    \s""");
            System.exit(0);
        }else
        {
            for(int i = 0; i<args.length; i++)
            {
                String currArg = args[i];
                if(currArg.equalsIgnoreCase("convert"))
                {
                    parsedInput.setFilepath(args[++i]);
                }
                if(currArg.equalsIgnoreCase("-t") || currArg.equalsIgnoreCase("-theme"))
                {
                    parsedInput.setThemeName(args[++i]);
                }
            }
        }
        return parsedInput;
    }
}
