package org.firstinspires.ftc.teamcode.tracking

data class DetectedObject (
    val x: Double,
    val y: Double,
    val radius: Double
)
fun parseDetectedObjects(centers: DoubleArray): MutableList<DetectedObject> {
    // Parse the centers array into a list of DetectedObject instances
    if(centers.size % 3 != 0) {
        throw IllegalArgumentException("Centers array must have a length that is a multiple of 3")
    }

    val detections = mutableListOf<DetectedObject>()
    for (i in centers.indices step 3) {
        val x = centers[i]
        val y = centers[i + 1]
        val radius = centers[i + 2]
        detections.add(DetectedObject(x, y, radius))
    }
    return detections
}