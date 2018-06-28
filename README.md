### Image tools

Compression in JPEG images is typically lossy, i.e. image quality is usually decreasing when loading and saving a JPEG image.
In some cases 

JPEG images are stored as a sequence of segments with the image data itself typically being the last segment. 
This implementation provides a reader for JPEG image segments. These segments can then be modified and written back to another JPEG image.

In case e.g. EXIF data needs to be removed from a JPEG image the corresponding segments can be removed and the image written back.
Since the image data is left untouched and no decompression and compression needed modifying image segments can be done without a lot of overhead.

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

