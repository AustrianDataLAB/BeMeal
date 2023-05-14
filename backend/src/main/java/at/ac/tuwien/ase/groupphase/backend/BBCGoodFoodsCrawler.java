package at.ac.tuwien.ase.groupphase.backend;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

public class BBCGoodFoodsCrawler {
    static final private String BBC_RECIPES = "https://www.bbcgoodfood.com/recipes/";
    static final private String IMAGE_PATH = "src/main/resources/recipes/";
    static final private String DATA_JSON_PATH = "database/neo4j/data.json";

    static private int successCounter = 0;
    static private int failedCounter = 0;

    public static void main(String[] args) {
        try {
            readDataFile();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println(successCounter);
            System.out.println(failedCounter);
        }
    }

    public static void readDataFile() throws IOException {
        File file = new File(DATA_JSON_PATH);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory jsonFactory = objectMapper.getFactory();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                JsonParser jsonParser = jsonFactory.createParser(line);
                // Read each JSON object
                while (jsonParser.nextToken() != null) {
                    // Process the JSON object
                    var row = objectMapper.readValue(jsonParser, Row.class);
                    if (row.page.recipe.pictureUUID == null) {
                        String sanitizedTitle = row.page.title.replace(' ', '-').toLowerCase().replaceAll("-&-", "-");
                        row.page.recipe.pictureUUID = crawlPicture(BBC_RECIPES + sanitizedTitle);
                    }
                    output.append(objectMapper.writeValueAsString(row)).append("\n");
                    System.out.print("|");
                    successCounter++;
                }
            }
            writeLineToFile(output.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeLineToFile(String content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("database/neo4j/data_out.json"));
        writer.write(content);
        writer.close();
    }

    public static String crawlPicture(String url) throws IOException {
        // Connect to the URL and fetch the HTML content
        Document document;
        try {
            document = Jsoup.connect(url).get();
        } catch (IOException e) {
            try {
                document = Jsoup.connect(url.replaceAll("-with-", "-")).get();
            } catch (IOException e1) {
                System.out.println();
                System.out.println("X");
                failedCounter++;
                return null;
            }
        }
        // Find the recipe picture element
        Elements pictureElements = document.select(".image__img");
        if (!pictureElements.isEmpty() && pictureElements.size() >= 3) {

            Element pictureElement = pictureElements.get(2);
            String imageUrl = pictureElement.attr("src");
            imageUrl = imageUrl.substring(0, imageUrl.indexOf('?'));

            // Download the image
            URL imageUrlObj = new URL(imageUrl);

            String uuid = String.valueOf(UUID.randomUUID());
            Path imagePath = Path.of(IMAGE_PATH + uuid + ".jpg"); // Path to save the image
            try {
                Files.copy(imageUrlObj.openStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e1) {
                System.out.println(e1.getMessage());
                return null;
            }
            return uuid;
        } else {
            System.out.println("Recipe picture not found on the page.");
        }
        return null;
    }

    public static class Row {
        @JsonProperty("page")
        private Page page;

        public static class Page {
            @JsonProperty("article")
            private Article article;

            @JsonProperty("recipe")
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private Recipe recipe;

            @JsonProperty("channel")
            private String channel;

            @JsonProperty("title")
            private String title;
        }

        public static class Article {
            @JsonProperty("author")
            private String author;

            @JsonProperty("description")
            private String description;

            @JsonProperty("id")
            private String id;

            @JsonProperty("tags")
            private List<String> tags;
        }

        public static class Recipe {
            @JsonProperty("collections")
            private List<String> collections;

            @JsonProperty("cooking_time")
            private int cookingTime;

            @JsonProperty("prep_time")
            private int prepTime;

            @JsonProperty("serves")
            private int serves;

            @JsonProperty("keywords")
            private List<String> keywords;

            @JsonProperty("ratings")
            private int ratings;

            @JsonProperty("nutrition_info")
            private List<String> nutritionInfo;

            @JsonProperty("ingredients")
            private List<String> ingredients;

            @JsonProperty("courses")
            private List<String> courses;

            @JsonProperty("cusine")
            private String cusine;

            @JsonProperty("diet_types")
            private List<String> dietTypes;

            @JsonProperty("picture_uuid")
            private String pictureUUID;

            @JsonProperty("skill_level")
            private String skillLevel;

            @JsonProperty("post_dates")
            private String postDates;
        }
    }
}