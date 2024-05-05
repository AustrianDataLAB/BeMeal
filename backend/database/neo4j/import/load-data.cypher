CALL apoc.load.json('file:///data.json') YIELD value

WITH value.page.article.id AS id,
     value.page.title AS title,
     value.page.article.description AS description,
     value.page.recipe.cooking_time AS cookingTime,
     value.page.recipe.prep_time AS preparationTime,
     value.page.recipe.skill_level AS skillLevel,
     value.page.recipe.picture_uuid AS picture,
     value.page.article.author AS author,
     value.page.recipe.ingredients AS ingredients,
     value.page.recipe.keywords AS keywords,
     value.page.recipe.diet_types AS dietTypes,
     value.page.recipe.collections AS collections
MERGE (r:Recipe {id: id})
SET r.cookingTime = cookingTime,
r.preparationTime = preparationTime,
r.name = title,
r.description = description,
r.skillLevel = skillLevel,
r.picture = picture
MERGE (a:Author {name: author})
MERGE (a)-[:WROTE]->(r)
FOREACH (ingredient IN ingredients |
  MERGE (i:Ingredient {name: ingredient})
  MERGE (r)-[:CONTAINS_INGREDIENT]->(i)
)
FOREACH (keyword IN keywords |
  MERGE (k:Keyword {name: keyword})
  MERGE (r)-[:KEYWORD]->(k)
)
FOREACH (dietType IN dietTypes |
  MERGE (d:DietType {name: dietType})
  MERGE (r)-[:DIET_TYPE]->(d)
)
FOREACH (collection IN collections |
  MERGE (c:Collection {name: collection})
  MERGE (r)-[:COLLECTION]->(c)
);
