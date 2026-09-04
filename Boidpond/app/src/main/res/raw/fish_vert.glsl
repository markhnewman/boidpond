uniform mat4 uMVPMatrix;
uniform float uTurn;
uniform float uSwim;
uniform float uAnim;
attribute vec4 aPosition;
varying lowp vec2 vColor;
void main() {
    vec2 pos = aPosition.xy;
    vColor = pos;

    vec2 p = pos;
    float t = p.x * uTurn - 0.03 * uSwim * sin(uAnim * 6.2831853);

    float swim = 0.1 * uSwim * smoothstep(0.25, -0.5, p.x);
    float angle = (0.4 * p.x + uAnim) * 6.2831853;
    pos.y += swim * sin(angle);
    pos.x -= p.y * swim * cos(angle);

    p = pos;
    pos.y += 1.0 * p.x * t - 2.0 * p.y * t * t;
    pos.x += -1.0 * p.y * t - 1.0 * p.x * t * t;

    gl_Position = uMVPMatrix * vec4(pos, 0.0, 1.0);
}
