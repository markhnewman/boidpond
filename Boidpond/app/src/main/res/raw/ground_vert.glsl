uniform mat4 uMVPMatrix;
attribute vec4 aPosition;
varying highp vec2 vCoord;
void main() {
    gl_Position = uMVPMatrix * aPosition;
    vCoord = aPosition.xy;
}
