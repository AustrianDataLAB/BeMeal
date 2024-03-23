import os
import json

############################################################################
neo4jData = "./data_old.json" # input with unicode artefacts
newFile = "./data.json"       # fixed output
picturesLocation = "../../src/main/resources/recipes"
############################################################################

pictures = [x.split(".")[0] for x in os.listdir(picturesLocation) if os.path.isfile(os.path.join(picturesLocation, x))]

names = set()

def isCorrectEntry(x: str):
    name: str = json.loads(x)["page"]["title"]
    if name in names: 
        print(f"duplicate entry: {name}")
        return False
    names.add(name)
    return ("null" not in x)\
          and (json.loads(x)["page"]["recipe"]["picture_uuid"] in pictures)\
          and len(json.loads(x)["page"]["recipe"]["ingredients"]) > 1

################## fix encoding artefacts ##################
with open(neo4jData, 'r', encoding='utf-8') as file:
    data: str = file.read()
    data = data.replace(u"\u00A0", " ")\
        .replace("’", "'")\
        .replace("–", "-")\
        .replace("\\\"", "'")
    data = data\
        .encode('latin-1', errors='ignore')\
        .decode('unicode_escape', errors='ignore')\
        .encode('latin-1', errors='ignore')\
        .decode('utf-8', errors='ignore')
    f = open(newFile, "w", encoding='utf-8')

    ########## remove corrupted data ##########
    correctEntries = [x for x in data.split("\n") if isCorrectEntry(x)]

    f.write("\n".join(correctEntries))
    f.close()
