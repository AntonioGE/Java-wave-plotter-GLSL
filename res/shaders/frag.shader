uniform vec2 windowSize;
uniform float zoom;
uniform vec2 mouse;


uniform int MAX_STEPS_RAY = 100;
uniform float MAX_DIST = 100.0;
uniform float SURF_DIST = 0.01;

const int MAX_NUM_POINTS = 10;
uniform vec2 pts[MAX_NUM_POINTS];
uniform int pts_enabled[MAX_NUM_POINTS];
uniform float t[MAX_NUM_POINTS];
uniform int num_pts = 0;

void main() {
    float x = (gl_FragCoord.x / windowSize.x - 0.5) * zoom;
    float y = (gl_FragCoord.y / windowSize.y - 0.5) * zoom;

    float value = 0.0;
    for(int i = 0; i < num_pts; i++){
        //value += sin(max(-distance(pts[i], vec2(x, y)) + t[i], 0));
        float dist = distance(pts[i], vec2(x, y));
        value += sin(max(-dist + t[i], 0)) / (1 + (t[i] - dist) * 0.1f);
    }

    value = value * 0.5 + 0.5;
    gl_FragColor = vec4(value, value, value, 1.0); 
}