#CUSTOM FILTERS
If one would like to search for files other than images, a person could make there own custom filter. Here's an example of the
`photos.json` filter.

*photos.json*
```json
{
  "mimes" : [
  	{"mime":"@image"},
	{"mime":"application/octet-stream","extRestrictions":[".png",".PNG"]}
], 
  "exts": []
}
```
That's the filter included for the default "searching for photos" functionality. Lets break down included functionality.

PFGUI can search in two ways. Either by mimetype, or extension.

###MIMETYPE

To search by mimetype, one simply needs to add a json object to the `mimes` array with a json String named `mime` with the data of 
the mimetype you'd like to search for. There are, however, some more involved features. For instance:

+ `@` Operator: When used in the `mime` string, this will search by generic type.

EX: If one includes `@audio` as an attribute, then any file with a mimetype that includes `audio` before the slash will be copied.
See `photos.json` above.

###MIMETYPE: Extension Restrictions

The `extRestrictions` json String array provides a way to blacklist or whitelist file extensions in your filter.

+ To whitelist, just include the extension in the `extRestrictions` array.
+ To blacklist, include the extension in the 'extRestrictions' array, but precede it with an `!` operator.

EX: the following json file.

*music.json*

```json
{
  "mimes" : [
  	{"mime":"@audio","extRestrictions":["!.mp4","!.mpeg"]}
], 
  "exts": []
}
```

###EXTENSION

To copy files based purely on extension, just include a json String in the extensions array.

EX: the following json file.

*redundant.json*
```json
{
  "mimes" : [], 
  "exts": [".json"]
}
```

##FILTER INSTALLATION
Just include your `json` file in **/Users/\<your username here\>/.store/PFGUI/filters/** directory.
