uniform sampler2D uTexture;
uniform lowp vec3 uColor;
varying highp vec2 vCoord;
void main() {
    lowp vec4 tex = texture2D(uTexture, vCoord);
    lowp float color = mix(tex.r, tex.b, 0.2);
    gl_FragColor = vec4(uColor * color / tex.a, tex.a);
}


