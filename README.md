### Image tools

JPEG images are stored as a sequence of segments with the image data itself typically being the last segment. 
This implementation provides a reader for JPEG image segments. These segments can then be modified and written back 
to another JPEG image.

In case e.g. EXIF data needs to be removed from a JPEG image the corresponding segments can be removed and 
the image written back. 
The image data is left untouched which means:
* no decompression and compression is performed, i.e. there is no loss of image quality due to JPEG typically using 
lossy compression
* manipulating the segments of the image can be done very fast since decompressing and compressing the image 
is not needed   

Usage is as follows:
```
// read JPEG image
val is = new FileInputStream(new File("path to file"))
val reader = JpegReader()
val jpeg = reader.read(is)

// remove COM (comment) segment
val withoutComments = jpeg.segments.filterNot(s => s.isInstanceOf[Com])
val targetJpeg = jpeg.copy(segments = withoutComments)

// write modified JPEG
val os = new FileOutputStream(new File("path to file"))
val writer = new JpegWriter
writer.write(targetJpeg, os)
```

### TODO

* Image streaming: currently the whole image data is read into memory. A streaming solution would reduce the required memory and basically allow unlimited image sizes. 
* Support for other formats (e.g. PNG)
* Support for reading and manipulating tags embedded into the metadata segments (e.g. EXIF) 