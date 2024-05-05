//set up indexes for query performance
CREATE INDEX idx_recipe FOR (n:Recipe) ON (n.id);

CREATE INDEX idx_ingredient FOR (n:Ingredient) ON (n.name);

CREATE INDEX idx_keyword FOR (n:Keyword) ON (n.name);

CREATE INDEX idx_diettype FOR (n:DietType) ON (n.name);

CREATE INDEX idx_author FOR (n:Author) ON (n.name);

CREATE INDEX idx_collection FOR (n:Collection) ON (n.name);
