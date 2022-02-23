package common.extent

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class StringExtent {
}

fun String.toPath(): Path {
    return Paths.get(this);
}

fun String.toFile(): File {
    return File(this);
}

