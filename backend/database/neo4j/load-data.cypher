//set up indexes for query performance
CREATE INDEX idx_recipe FOR (n:Recipe) ON (n.id);
CREATE INDEX idx_ingredient FOR (n:Ingredient) ON (n.name);
CREATE INDEX idx_keyword FOR (n:Keyword) ON (n.name);
CREATE INDEX idx_diettype FOR (n:DietType) ON (n.name);
CREATE INDEX idx_author FOR (n:Author) ON (n.name);
CREATE INDEX idx_collection FOR (n:Collection) ON (n.name);

//import recipes to the graph
CALL apoc.load.json("file:///data.json") YIELD value
WITH value.page.article.id AS id,
       value.page.title AS title,
       value.page.article.description AS description,
       value.page.recipe.cooking_time AS cookingTime,
       value.page.recipe.prep_time AS preparationTime,
       value.page.recipe.skill_level AS skillLevel,
       value.page.recipe.picture_uuid as picture
MERGE (r:Recipe {id: id})
SET r.cookingTime = cookingTime,
    r.preparationTime = preparationTime,
    r.name = title,
    r.description = description,
    r.skillLevel = skillLevel,
    r.picture = picture;

//import authors and connect to recipes
CALL apoc.load.json("file:///data.json") YIELD value
WITH value.page.article.id AS id,
       value.page.article.author AS author
MERGE (a:Author {name: author})
WITH a,id
MATCH (r:Recipe {id:id})
MERGE (a)-[:WROTE]->(r);

//import ingredients and connect to recipes
CALL apoc.load.json("file:///data.json") YIELD value
WITH value.page.article.id AS id,
       value.page.recipe.ingredients AS ingredients
MATCH (r:Recipe {id:id})
FOREACH (ingredient IN ingredients |
  MERGE (i:Ingredient {name: ingredient})
  MERGE (r)-[:CONTAINS_INGREDIENT]->(i)
);

//import keywords and connect to recipes
CALL apoc.load.json("file:///data.json") YIELD value
WITH value.page.article.id AS id,
       value.page.recipe.keywords AS keywords
MATCH (r:Recipe {id:id})
FOREACH (keyword IN keywords |
  MERGE (k:Keyword {name: keyword})
  MERGE (r)-[:KEYWORD]->(k)
);

//import dietTypes and connect to recipes
CALL apoc.load.json("file:///data.json") YIELD value
WITH value.page.article.id AS id,
       value.page.recipe.diet_types AS dietTypes
MATCH (r:Recipe {id:id})
FOREACH (dietType IN dietTypes |
  MERGE (d:DietType {name: dietType})
  MERGE (r)-[:DIET_TYPE]->(d)
);

//import collections and connect to recipes
CALL apoc.load.json("file:///data.json") YIELD value
WITH value.page.article.id AS id,
       value.page.recipe.collections AS collections
MATCH (r:Recipe {id:id})
FOREACH (collection IN collections |
  MERGE (c:Collection {name: collection})
  MERGE (r)-[:COLLECTION]->(c)
);