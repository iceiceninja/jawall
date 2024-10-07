package org.example;

import lombok.Getter;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.InputStream;

@Getter
public class ConfigManager {
    private Themes themeSet;

    public ConfigManager(String filename)
    {
        try {
            // Load the YAML file
            InputStream inputStream = new FileInputStream(filename);

            // Create a new Yaml object and parse the file into the Themes class
            Yaml yaml = new Yaml(new Constructor(Themes.class,new LoaderOptions()));
            themeSet = yaml.load(inputStream);

//            for (Theme theme : themeSet.getThemes()) {
//                System.out.println("Theme: " + theme.getName());
//                List<String> colors = theme.getColors();
//                System.out.println("Colors: " + colors);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
