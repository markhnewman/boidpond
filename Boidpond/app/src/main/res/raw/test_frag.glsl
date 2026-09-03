varying lowp vec2 vColor;
void main() {
    lowp vec2 color = floor(8.0 * vColor);
    lowp float c = floor(mod(color.x + color.y + 0.5, 2.0));
    gl_FragColor = vec4(c, c, c, 1.0);
}
