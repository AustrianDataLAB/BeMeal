############################################################################
neo4jData = "./data_old.json" # input with unicode artefacts
newFile = "./data.json"       # fixed output
############################################################################

with open(neo4jData, 'r', encoding='utf-8') as file:
    data: str = file.read()
    data = data.replace(u"\u00A0", " ")\
        .replace("’", "'")\
        .replace("–", "-")
    data = data\
        .encode('latin-1', errors='ignore')\
        .decode('unicode_escape', errors='ignore')\
        .encode('latin-1', errors='ignore')\
        .decode('utf-8', errors='ignore')
    f = open(newFile, "w", encoding='utf-8')
    f.write(data)
    f.close()
