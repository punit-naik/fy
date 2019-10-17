# Fy

Fy Programming Challenge

## Usage

**NOTE**: You will require JDK + Leiningen to use this project

### Run

```
lein run

# OR

lein run <port>
```

This will start the server on the port you have provided (by default `3000`).

### Test (code - unit)

```
lein test
```

### Test (app)

Go to the browser and type in the following url in the address bar:

```
localhost:<port>/feed?width=1000&height=1000&limit=4
```
Then you'll see the download window pop up where you can save the images archived in a ZIP.

*OR* you can also go to `http://localhost:<port>/swagger-ui` and test there. *BTW*, file download does not really work with swagger, see [swagger's](https://swagger.io/) github [ticket](https://github.com/swagger-api/swagger-ui/issues/1605). It works from the browser though :+1:

**NOTE**:
1. `width` and `height` are specified in pixels, used for resizing the Flickr public photo feed images. If supplied, all the images are resized to the `width` and `height` values the user provided.
2. Resizing only works if the user provides the values of `width` and `height` together. If only one o them are provided, it will simply ignore it and won't resize the images.
3. Setting the value of `limit` will limit the number of images fetched from the Flickr public photo feed.
4. The quality of the images fetched before resizing is the deault one provided by the URLs in the Flickr public photo feed.