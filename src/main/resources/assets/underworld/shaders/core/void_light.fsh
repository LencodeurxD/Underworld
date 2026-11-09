#version 150

uniform sampler2D Depth;
uniform mat4 InvViewProj;
uniform vec3 CameraPos;
uniform float Time;

uniform vec4 Params;

uniform vec4 Params2;

uniform vec4 Params3;

uniform vec4 Params4;

uniform vec4 Params5;

uniform vec4 Params8;

uniform vec4 Params9;

uniform vec4 Params10;

uniform vec4 Params12;

uniform vec4 Params13;

uniform vec4 Params14;

uniform vec4 Params15;

uniform vec4 Params16;

uniform vec4 IslandA0;
uniform vec4 IslandA1;
uniform vec4 IslandA2;
uniform vec4 IslandA3;
uniform vec4 IslandA4;
uniform vec4 IslandA5;
uniform vec4 IslandA6;
uniform vec4 IslandA7;
uniform vec4 IslandA8;
uniform vec4 IslandA9;
uniform vec4 IslandA10;
uniform vec4 IslandA11;
uniform vec4 IslandA12;
uniform vec4 IslandA13;
uniform vec4 IslandA14;
uniform vec4 IslandA15;
uniform vec4 IslandA16;
uniform vec4 IslandA17;
uniform vec4 IslandA18;
uniform vec4 IslandA19;
uniform vec4 IslandA20;
uniform vec4 IslandA21;
uniform vec4 IslandA22;
uniform vec4 IslandA23;
uniform vec4 IslandA24;
uniform vec4 IslandA25;
uniform vec4 IslandA26;
uniform vec4 IslandA27;
uniform vec4 IslandA28;
uniform vec4 IslandA29;
uniform vec4 IslandA30;
uniform vec4 IslandA31;

uniform vec4 Island0;
uniform vec4 Island1;
uniform vec4 Island2;
uniform vec4 Island3;
uniform vec4 Island4;
uniform vec4 Island5;
uniform vec4 Island6;
uniform vec4 Island7;
uniform vec4 Island8;
uniform vec4 Island9;
uniform vec4 Island10;
uniform vec4 Island11;
uniform vec4 Island12;
uniform vec4 Island13;
uniform vec4 Island14;
uniform vec4 Island15;
uniform vec4 Island16;
uniform vec4 Island17;
uniform vec4 Island18;
uniform vec4 Island19;
uniform vec4 Island20;
uniform vec4 Island21;
uniform vec4 Island22;
uniform vec4 Island23;
uniform vec4 Island24;
uniform vec4 Island25;
uniform vec4 Island26;
uniform vec4 Island27;
uniform vec4 Island28;
uniform vec4 Island29;
uniform vec4 Island30;
uniform vec4 Island31;

in vec2 texCoord;
out vec4 fragColor;

vec4 gIsl[32];
vec4 gAx[32];
int gN;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float vnoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i), hash(i + vec2(1.0, 0.0)), f.x),
               mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), f.x), f.y);
}

float hash3(vec3 p) {
    p = fract(p * 0.3183099 + 0.1);
    p *= 17.0;
    return fract(p.x * p.y * p.z * (p.x + p.y + p.z));
}

float vnoise3(vec3 x) {
    vec3 i = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix(hash3(i + vec3(0.0, 0.0, 0.0)), hash3(i + vec3(1.0, 0.0, 0.0)), f.x),
                   mix(hash3(i + vec3(0.0, 1.0, 0.0)), hash3(i + vec3(1.0, 1.0, 0.0)), f.x), f.y),
               mix(mix(hash3(i + vec3(0.0, 0.0, 1.0)), hash3(i + vec3(1.0, 0.0, 1.0)), f.x),
                   mix(hash3(i + vec3(0.0, 1.0, 1.0)), hash3(i + vec3(1.0, 1.0, 1.0)), f.x), f.y), f.z);
}

float fbm3(vec3 p) {
    float a = 0.5;
    float s = 0.0;
    for (int k = 0; k < 3; k++) {
        s += a * vnoise3(p);
        p = p * 2.03 + vec3(7.1, 3.3, 5.7);
        a *= 0.5;
    }
    return s / 0.875;
}

float mistPattern(vec3 p) {
    vec3 q = p * Params5.y;
    float t = Time * Params5.z;
    float r1 = 1.0 - abs(2.0 * vnoise3(q + vec3(t, 0.3 * t, -0.6 * t)) - 1.0);
    float r2 = 1.0 - abs(2.0 * vnoise3(q * 1.9 + vec3(-0.7 * t, t, 0.5 * t) + 31.7) - 1.0);
    float v1 = smoothstep(0.80, 1.0, r1);
    float v2 = smoothstep(0.80, 1.0, r2);
    return clamp(v1 * 0.65 + v2 * 0.45 + v1 * v2 * 0.6, 0.0, 1.0);
}

const vec3 SUN = vec3(0.363, 0.884, 0.295);
const vec3 SUN_L = vec3(0.622, 0.401, 0.672);

float fbm2(vec2 p) {
    float a = 0.5;
    float s = 0.0;
    for (int k = 0; k < 4; k++) {
        s += a * vnoise(p);
        p = p * 2.03 + vec2(5.2, 1.7);
        a *= 0.5;
    }
    return s / 0.9375;
}

vec3 lightSky(vec3 dir) {
    float up = dir.y;
    vec3 col = mix(vec3(1.00, 0.80, 0.68), vec3(0.50, 0.80, 1.00), smoothstep(0.0, 0.28, up));
    col = mix(col, vec3(0.10, 0.38, 0.92), smoothstep(0.22, 0.95, up));
    col = mix(col, vec3(0.80, 0.86, 1.00), smoothstep(0.0, -0.5, up));
    float sd = max(dot(dir, SUN_L), 0.0);
    float ang = acos(min(sd, 1.0));
    col += vec3(1.00, 0.55, 0.35) * exp(-ang * 3.2) * 0.55;
    col += vec3(1.00, 0.88, 0.60) * (pow(sd, 700.0) * 5.0 + pow(sd, 60.0) * 0.7 + pow(sd, 8.0) * 0.25);
    vec3 t1 = normalize(cross(SUN_L, vec3(0.0, 1.0, 0.0)));
    vec3 t2 = cross(SUN_L, t1);
    float a = atan(dot(dir, t2), dot(dir, t1));
    float rays = vnoise(vec2(a * 6.0, Time * 0.05)) * 0.6 + vnoise(vec2(a * 17.0 + 4.0, -Time * 0.08)) * 0.4;
    col += vec3(1.00, 0.85, 0.60) * pow(sd, 5.0) * smoothstep(0.40, 0.95, rays) * 0.45;

    float ar = acos(clamp(dot(dir, -SUN_L), -1.0, 1.0));
    float x = (ar - 0.70) / 0.075;
    float band = clamp(1.0 - abs(x - 0.5) * 2.0, 0.0, 1.0);
    float hue = clamp(x, 0.0, 1.0);
    vec3 rb = vec3(clamp(1.5 - abs(hue * 4.0 - 3.0), 0.0, 1.0), clamp(1.5 - abs(hue * 4.0 - 2.0), 0.0, 1.0), clamp(1.5 - abs(hue * 4.0 - 1.0), 0.0, 1.0));
    col += rb * pow(band, 1.5) * 0.30 * smoothstep(-0.08, 0.12, up);
    vec2 sdir = normalize(SUN_L.xz);
    if (up > 0.005) {
        float k = 1.0 / (up + 0.20);
        vec2 q = dir.xz * k * 0.9 + vec2(Time * 0.012, Time * 0.006);
        float c = smoothstep(0.50, 0.74, fbm2(q));
        float c2 = smoothstep(0.50, 0.74, fbm2(q + sdir * 0.10));
        float shade = clamp(0.55 + (c - c2) * 5.0, 0.0, 1.0);
        vec3 cloud = mix(vec3(0.60, 0.68, 0.90), vec3(1.00, 0.97, 0.93), shade);
        cloud += vec3(1.00, 0.65, 0.45) * exp(-ang * 3.0) * 0.35 * shade;
        col = mix(col, cloud, c * 0.95 * smoothstep(0.005, 0.20, up));
    }
    if (up < -0.005) {
        float k = 1.0 / (-up + 0.24);
        vec2 q = dir.xz * k * 1.1 + vec2(-Time * 0.010, Time * 0.007);
        float c = smoothstep(0.40, 0.72, fbm2(q));
        float c2 = smoothstep(0.40, 0.72, fbm2(q + sdir * 0.12));
        float shade = clamp(0.55 + (c - c2) * 4.0, 0.0, 1.0);
        vec3 sea = mix(vec3(0.70, 0.78, 0.96), vec3(1.00, 0.98, 0.96), shade);
        sea += vec3(1.00, 0.70, 0.50) * exp(-ang * 2.5) * 0.30;
        col = mix(col, sea, c * 0.9 * smoothstep(0.005, 0.15, -up));
    }
    return col;
}

vec3 darkSky(vec3 dir) {
    float up = dir.y;
    vec3 col = mix(vec3(0.09, 0.015, 0.16), vec3(0.004, 0.0, 0.016), smoothstep(0.0, 0.7, up));
    col = mix(col, vec3(0.14, 0.01, 0.05), smoothstep(0.0, -0.5, up));
    float sd = clamp(dot(dir, SUN), -1.0, 1.0);
    float ang = acos(sd);
    float disc = 1.0 - smoothstep(0.050, 0.054, ang);
    if (up > -0.05) {
        for (int k = 0; k < 3; k++) {
            vec3 p = dir * (60.0 + 55.0 * float(k));
            vec3 ip = floor(p);
            vec3 fp = p - ip;
            float h = hash3(ip + float(k) * 17.3);
            if (h > 0.965) {
                vec3 c = vec3(hash3(ip + 3.1), hash3(ip + 7.7), hash3(ip + 11.9)) * 0.6 + 0.2;
                float tw = 0.65 + 0.35 * sin(Time * (1.5 + 3.0 * h) + h * 40.0);
                vec3 starCol = mix(vec3(0.75, 0.85, 1.00), vec3(1.00, 0.75, 0.90), fract(h * 53.0));
                col += starCol * smoothstep(0.30, 0.0, length(fp - c)) * tw * 1.4;
            }
        }
    }
    if (up > 0.05) {
        float a = fbm2(vec2(dir.x * 2.4 + Time * 0.015, dir.z * 2.4 - Time * 0.010));
        float band = smoothstep(0.52, 0.68, a) * smoothstep(0.05, 0.40, up) * (1.0 - smoothstep(0.65, 0.95, up));
        float hue = fbm2(vec2(dir.z * 1.7 - Time * 0.020, dir.x * 1.7 + 9.0));
        col += mix(vec3(0.55, 0.05, 0.85), vec3(0.05, 0.85, 0.65), hue) * band * 0.9;
    }
    if (up > 0.01) {
        vec2 q = dir.xz / (up + 0.2) * 0.8 + vec2(-Time * 0.010, Time * 0.012);
        float c = smoothstep(0.50, 0.80, fbm2(q + 20.0));
        col = mix(col, vec3(0.010, 0.002, 0.020), c * 0.8 * smoothstep(0.01, 0.2, up));
    }
    if (up < -0.01) {
        vec2 q = dir.xz / (-up + 0.25) + vec2(Time * 0.008, 0.0);
        float c = smoothstep(0.45, 0.80, fbm2(q + 40.0));
        col += vec3(0.30, 0.04, 0.02) * c * 0.8 * smoothstep(0.01, 0.2, -up);
    }

    float cq = (ang - 0.058) / 0.014;
    float corona = exp(-cq * cq) * 1.3 + exp(-ang * 9.0) * 0.28;
    vec3 coronaCol = mix(vec3(0.55, 0.12, 0.95), vec3(1.00, 0.55, 0.95), exp(-ang * 14.0));
    col = col * (1.0 - disc) + coronaCol * corona * (1.0 - disc);
    return col;
}

vec3 motes(vec3 dir, float tMax, bool lit) {
    vec3 acc = vec3(0.0);
    for (int k = 0; k < 7; k++) {
        float dist = 8.0 * pow(1.45, float(k));
        if (dist > tMax) break;
        vec3 q = CameraPos + dir * dist;
        q.y += lit ? Time * 0.6 : -Time * 0.35;
        float cs = dist * 0.12;
        vec3 ip = floor(q / cs);
        float h = hash3(ip + float(k) * 3.7);
        if (h < 0.75) continue;
        vec3 c = (ip + vec3(hash3(ip + 1.3), hash3(ip + 5.1), hash3(ip + 9.9))) * cs;
        float d = length(q - c);
        float r = dist * 0.0035;
        float tw = 0.5 + 0.5 * sin(Time * (2.0 + 5.0 * h) + h * 60.0);
        acc += (lit ? vec3(1.00, 0.85, 0.45) : vec3(1.00, 0.35, 0.12)) * exp(-(d * d) / (r * r)) * tw * 1.5;
    }
    return acc;
}

vec3 viewPointAt(vec2 uv) {
    float z = texture(Depth, uv).r;
    vec4 q = InvViewProj * vec4(uv * 2.0 - 1.0, z * 2.0 - 1.0, 1.0);
    return q.xyz / q.w;
}

vec3 surfaceNormal() {
    vec2 px = 1.0 / vec2(textureSize(Depth, 0));
    vec3 c = viewPointAt(texCoord);
    vec3 r = viewPointAt(texCoord + vec2(px.x, 0.0));
    vec3 l = viewPointAt(texCoord - vec2(px.x, 0.0));
    vec3 u = viewPointAt(texCoord + vec2(0.0, px.y));
    vec3 d = viewPointAt(texCoord - vec2(0.0, px.y));
    vec3 dx = (length(r - c) < length(c - l)) ? (r - c) : (c - l);
    vec3 dy = (length(u - c) < length(c - d)) ? (u - c) : (c - d);
    vec3 n = cross(dx, dy);
    float len = length(n);
    if (len < 1e-6) return vec3(0.0, 1.0, 0.0);
    n /= len;
    if (dot(n, c) > 0.0) n = -n;
    return n;
}

vec3 viewPoint(float z) {
    vec4 p = InvViewProj * vec4(texCoord * 2.0 - 1.0, z * 2.0 - 1.0, 1.0);
    return p.xyz / p.w;
}

void bubbleLayer(int idx, vec3 dir, float tMax, out vec4 fill, out vec4 rim) {
    fill = vec4(0.0);
    rim = vec4(0.0);
    vec4 s = gIsl[idx];
    vec3 ax = gAx[idx].xyz;
    if (abs(s.w) < 1.0 || Params13.w < 0.5 || ax.x < 1.0) return;
    vec3 oc = (CameraPos - s.xyz) / ax;
    vec3 dd = dir / ax;
    float a = dot(dd, dd);
    float b = dot(oc, dd);
    float cc = dot(oc, oc) - 1.0;
    float disc = b * b - a * cc;
    bool camIn = cc < 0.0;
    bool lit = s.w > 0.0;

    float t0 = -1.0;
    float t1 = -1.0;
    float halo = 0.0;
    if (camIn) {
        if (disc <= 0.0) return;
        t1 = (-b + sqrt(disc)) / a;
    } else {
        float tc = -b / a;
        if (tc <= 0.0) return;
        if (disc > 0.0) {
            float sq = sqrt(disc) / a;
            t0 = tc - sq;
            t1 = tc + sq;
        } else if (tc < tMax) {
            float perp = sqrt(max(dot(oc, oc) - b * b / a, 0.0));
            halo = 1.0 - smoothstep(1.0, 1.18, perp);
        }
    }

    float tHit = -1.0;
    vec3 hu = vec3(0.0);
    float wallFade = 1.0;
    float seam = 0.0;
    float tintK = 0.0;
    float fade0 = 1.0;
    for (int h = 0; h < 2; h++) {
        float t = (h == 0) ? t0 : t1;
        if (t <= 0.0 || t >= tMax) continue;
        vec3 u = oc + dd * t;
        vec3 hw = s.xyz + u * ax;
        float fade = 1.0;
        float sm = 0.0;
        float tk = 0.0;
        for (int j = 0; j < 32; j++) {
            if (j >= gN) break;
            if (j == idx) continue;
            vec4 o = gIsl[j];
            vec3 oa = gAx[j].xyz;
            if (abs(o.w) < 1.0 || oa.x < 1.0) continue;
            vec3 q = (hw - o.xyz) / max(oa, vec3(1.0));
            float m = dot(q, q);
            float outsideK = smoothstep(0.985, 1.20, m);
            sm = max(sm, exp(-(m - 1.0) * (m - 1.0) / 0.0025));
            if (o.w * s.w > 0.0) {
                fade = min(fade, outsideK);
            } else if (abs(o.w) > abs(s.w) || (abs(o.w) == abs(s.w) && j < idx)) {
                fade = min(fade, outsideK);
            } else {
                tk = max(tk, 1.0 - smoothstep(0.45, 1.0, m));
            }
        }
        if (h == 0) fade0 = fade;
        if (fade < 0.03) continue;
        tHit = t; hu = u; wallFade = fade; seam = sm; tintK = tk;
        break;
    }

    float chord = 0.0;
    if (!camIn && t0 > 0.0 && t0 < tMax) chord = min(t1, tMax) - t0;
    if (tHit < 0.0 && halo <= 0.0 && chord <= 0.0) return;

    float film = 0.0;
    float edge = 0.0;
    vec3 hp = vec3(0.0);
    bool backFace = camIn || (tHit > 0.0 && t0 > 0.0 && tHit > t0 + 0.001);
    if (tHit > 0.0) {
        vec3 n = normalize(hu / ax);
        edge = abs(dot(n, dir));
        hp = hu * ax;
        float fr = max(1.0 - edge, 0.0);
        film = backFace ? (0.45 + 0.55 * fr * fr) : (0.30 + 0.70 * pow(fr, 1.3));
    }
    float ripple = vnoise3(hp * 0.05 + vec3(Time * 0.04, -Time * 0.03, Time * 0.02));
    float swirl = vnoise3(hp * 0.018 + vec3(-Time * 0.03, Time * 0.05, 0.0));
    vec3 irid = 0.5 + 0.5 * cos(6.2831853 * (vec3(0.0, 0.33, 0.67) + edge * 0.8 + ripple * 0.6 + swirl * 0.5 + Time * 0.02));
    vec3 baseCol = lit ? vec3(1.00, 0.90, 0.55) : vec3(0.45, 0.18, 0.95);
    vec3 otherCol = lit ? vec3(0.45, 0.18, 0.95) : vec3(1.00, 0.90, 0.55);
    baseCol = mix(baseCol, otherCol, tintK);
    float ra;
    if (camIn) {
        float prox = smoothstep(0.70, 1.0, sqrt(dot(oc, oc)));
        float fe = max(1.0 - edge, 0.0);
        float dens = clamp(Params16.w, 0.0, 1.0);
        float baseA = mix(0.30, 0.82, dens) + mix(0.62, 0.15, dens) * pow(fe, 1.4);
        ra = clamp(baseA * mix(0.72 + 0.28 * swirl, 0.86 + 0.14 * swirl, dens) + prox * mix(0.30, 0.10, dens), 0.0, mix(0.85, 0.97, dens)) * Params16.x;
        if (tHit < 0.0) ra = 0.0;
    } else {
        ra = clamp(film * (0.75 + 0.25 * ripple) * 1.05 + halo * 0.55, 0.0, 0.85) * Params14.z;
    }
    vec3 rimCol = mix(baseCol, irid, 0.5);
    if (camIn && tHit > 0.0) {
        float vein = 1.0 - abs(2.0 * vnoise3(hp * 0.07 + vec3(Time * 0.05, Time * 0.11, -Time * 0.04)) - 1.0);
        rimCol += vec3(1.00, 0.95, 0.85) * smoothstep(0.80, 1.0, vein) * 0.45 * clamp(Params16.w, 0.0, 1.0) * clamp(Params15.w, 0.0, 1.0);
    }
    ra *= wallFade;
    float wallK = camIn ? Params16.x : Params14.z;
    float seamK = seam * wallK * clamp(Params14.w, 0.0, 1.0) * ((tHit > 0.0) ? 1.0 : 0.0);
    rimCol = mix(rimCol, vec3(1.00, 0.95, 0.85), seamK * 0.8);
    ra = max(ra, seamK * 0.9);
    rim = vec4(clamp(rimCol, 0.0, 1.0), clamp(ra, 0.0, 0.97));
    float fa = (1.0 - exp(-chord * 0.012)) * (lit ? Params14.x : Params14.y) * fade0;
    fill = vec4(lit ? vec3(1.00, 0.90, 0.60) : vec3(0.05, 0.02, 0.10), fa);
}

void overLayer(inout vec3 C, inout float A, vec3 c, float a) {
    C = C * (1.0 - a) + c * a;
    A = A + a * (1.0 - A);
}

const int DEBUG_MODE = 0;

void main() {
    if (Params.x < 0.0) {
        fragColor = vec4(1.0, 0.0, 1.0, 1.0);
        return;
    }

    float d = texture(Depth, texCoord).r;
    vec3 dir = normalize(viewPoint(1.0));

    bool bg = d >= 0.99999;
    if (!bg && Params12.x < 0.5) discard;

    if (!bg && max(Params13.x, Params13.y) > 0.02) {
        if (CameraPos.y + viewPoint(d).y > 248.0) bg = true;
    }

    vec3 hitRel = bg ? dir * Params.w : viewPoint(d);
    float hitY = CameraPos.y + hitRel.y;
    float dist = length(hitRel);

    if (DEBUG_MODE == 3 && !bg) {
        fragColor = vec4(0.0);
        return;
    }
    if (DEBUG_MODE == 1 && !bg) {
        fragColor = (dist < 8.0) ? vec4(0.0, 1.0, 0.0, 1.0) : vec4(1.0, 0.0, 0.0, 1.0);
        return;
    }

    float maxT = min(Params.w, dist);

    float bLm = Params13.x;
    float bDm = Params13.y;
    bool inB = (bLm > 0.001 || bDm > 0.001);

    const int STEPS = 28;
    float stepLen = maxT / float(STEPS);
    float t = stepLen * hash(gl_FragCoord.xy + fract(Time));
    float accL = 0.0;
    float accD = 0.0;
    float accS = 0.0;
    float accF = 0.0;
    float accBL = 0.0;
    float accBD = 0.0;

    for (int i = 0; i < STEPS; i++) {
        vec3 p = CameraPos + dir * t;

        float fallL = exp(-max(p.y - Params.y, 0.0) / Params.z);
        float nL  = vnoise(p.xz * 0.040 + vec2(Time * 0.020, -Time * 0.015));
        float nL2 = vnoise(p.xz * 0.110 - vec2(Time * 0.030,  Time * 0.010));
        float shaftsL = smoothstep(0.50, 0.90, nL * 0.7 + nL2 * 0.3);
        float flowL = 0.55 + 0.45 * vnoise(vec2(p.y * 0.045 - Time * Params2.w, (p.x + p.z * 1.7) * 0.06));
        accL += fallL * mix(0.08, 1.0, shaftsL) * flowL * stepLen;

        float fallD = exp(-max(Params2.y - p.y, 0.0) / Params2.z);
        float nD  = vnoise(p.xz * 0.050 + vec2(-Time * 0.012, Time * 0.018) + 37.0);
        float nD2 = vnoise(p.xz * 0.130 + vec2(Time * 0.020, Time * 0.025) - 19.0);
        float shaftsD = smoothstep(0.35, 0.85, nD * 0.7 + nD2 * 0.3);
        float flowD = 0.55 + 0.45 * vnoise(vec2(p.y * 0.045 + Time * Params2.w, (p.x - p.z * 1.3) * 0.06 + 11.0));
        accD += fallD * mix(0.15, 1.0, shaftsD) * flowD * stepLen;

        if (p.y > Params3.y) {
            float layer = smoothstep(Params3.y, Params2.y + 5.0, p.y);
            layer *= layer;
            vec3 q = p * 0.030;
            q.y -= Time * Params3.z;
            q.xz += vec2(sin(q.y * 2.3 + Time * 0.05), cos(q.y * 1.9 - Time * 0.04)) * 0.4;
            float sn = fbm3(q);
            accS += smoothstep(0.30, 0.75, sn) * layer * stepLen;
        }

        if (Params5.x > 0.0) {
            float mpat = mistPattern(p);
            float altF = 0.35 + 0.65 * exp(-max(p.y - Params.y, 0.0) / (Params.z * 1.5));
            float dens = mix(1.0, mpat, Params5.w);
            accF += dens * altF * stepLen;
        }

        if (inB) {
            vec3 q = p * 0.022 + vec3(Time * 0.010, -Time * 0.018, Time * 0.008);
            float fb = fbm3(q);
            accBL += bLm * smoothstep(0.42, 0.80, fb) * stepLen;
            accBD += bDm * smoothstep(0.34, 0.74, fb) * stepLen;
        }

        t += stepLen;
    }

    float aL = 1.0 - exp(-accL * 0.02 * Params.x);
    float aD = min(1.0 - exp(-(accD * Params2.x + accS * Params3.x) * 0.02), 0.95);
    float aF = min(1.0 - exp(-accF * 0.005 * Params5.x), 0.6);
    float aBL = min(1.0 - exp(-accBL * 0.025 * Params15.z), 0.75);
    float aBD = min(1.0 - exp(-accBD * 0.030 * Params15.z), 0.85);
    const vec3 MIST_COL = vec3(0.85, 0.93, 1.00);

    gIsl[0] = Island0;
    gAx[0] = IslandA0;
    gIsl[1] = Island1;
    gAx[1] = IslandA1;
    gIsl[2] = Island2;
    gAx[2] = IslandA2;
    gIsl[3] = Island3;
    gAx[3] = IslandA3;
    gIsl[4] = Island4;
    gAx[4] = IslandA4;
    gIsl[5] = Island5;
    gAx[5] = IslandA5;
    gIsl[6] = Island6;
    gAx[6] = IslandA6;
    gIsl[7] = Island7;
    gAx[7] = IslandA7;
    gIsl[8] = Island8;
    gAx[8] = IslandA8;
    gIsl[9] = Island9;
    gAx[9] = IslandA9;
    gIsl[10] = Island10;
    gAx[10] = IslandA10;
    gIsl[11] = Island11;
    gAx[11] = IslandA11;
    gIsl[12] = Island12;
    gAx[12] = IslandA12;
    gIsl[13] = Island13;
    gAx[13] = IslandA13;
    gIsl[14] = Island14;
    gAx[14] = IslandA14;
    gIsl[15] = Island15;
    gAx[15] = IslandA15;
    gIsl[16] = Island16;
    gAx[16] = IslandA16;
    gIsl[17] = Island17;
    gAx[17] = IslandA17;
    gIsl[18] = Island18;
    gAx[18] = IslandA18;
    gIsl[19] = Island19;
    gAx[19] = IslandA19;
    gIsl[20] = Island20;
    gAx[20] = IslandA20;
    gIsl[21] = Island21;
    gAx[21] = IslandA21;
    gIsl[22] = Island22;
    gAx[22] = IslandA22;
    gIsl[23] = Island23;
    gAx[23] = IslandA23;
    gIsl[24] = Island24;
    gAx[24] = IslandA24;
    gIsl[25] = Island25;
    gAx[25] = IslandA25;
    gIsl[26] = Island26;
    gAx[26] = IslandA26;
    gIsl[27] = Island27;
    gAx[27] = IslandA27;
    gIsl[28] = Island28;
    gAx[28] = IslandA28;
    gIsl[29] = Island29;
    gAx[29] = IslandA29;
    gIsl[30] = Island30;
    gAx[30] = IslandA30;
    gIsl[31] = Island31;
    gAx[31] = IslandA31;
    gN = int(Params12.y + 0.5);

    float effW = 1.0 - smoothstep(Params4.x, Params4.y, abs(dir.y));

    if (bg) {
        float soft = max(Params3.w, 0.0001);
        float th = clamp(max(CameraPos.y, 0.0) / max(Params4.w, 1.0) + Params10.z, 0.0, 0.9);
        float belowK = 1.0 - smoothstep(-th - soft, -th + soft, dir.y);
        float base = mix(belowK, 1.0 - belowK, Params13.z) * Params4.z;
        aL *= effW;
        aD *= effW;
        aF *= effW;
        float up = smoothstep(-0.05, 0.25, dir.y);
        aF *= mix(1.0, Params10.w, up);
        aL *= mix(1.0, sqrt(Params10.w), up);
        vec3 colBg = mix(vec3(0.35, 0.65, 1.00), vec3(1.00, 0.95, 0.85), aL * aL);
        vec3 res = vec3(base) * (1.0 - aD) * (1.0 - aF) + MIST_COL * aF * (1.0 - aD) + colBg * aL;

        if (inB) {
            float pat = mistPattern(CameraPos + dir * 80.0);
            float upK = 0.5 + 0.5 * dir.y;
            vec3 flatL = mix(vec3(1.00, 0.80, 0.45), vec3(1.00, 0.95, 0.78), upK) * (0.85 + 0.15 * pat);
            vec3 flatD = mix(vec3(0.010, 0.004, 0.020), vec3(0.030, 0.012, 0.055), upK) + vec3(0.06, 0.02, 0.12) * pat * 0.30;
            vec3 lightWorld = flatL;
            vec3 darkWorld = flatD;
            if (Params16.y > 0.001) {
                if (bLm > 0.001) lightWorld = mix(flatL, lightSky(dir), Params16.y);
                if (bDm > 0.001) darkWorld = mix(flatD, darkSky(dir), Params16.y);
            }
            res = mix(res, lightWorld, bLm);
            res = mix(res, darkWorld, bDm);
            res = mix(res, vec3(1.00, 0.99, 0.94), aBL * 0.55);
            res = mix(res, vec3(0.16, 0.05, 0.30), aBD * 0.55);
            if (Params16.z > 0.001) {
                res += motes(dir, 1.0e9, true) * bLm * Params16.z;
                res += motes(dir, 1.0e9, false) * bDm * Params16.z;
            }
        }

        vec3 portalL = vec3(0.0);
        vec3 portalD = vec3(0.0);
        bool haveL = false;
        bool haveD = false;
        for (int k = 31; k >= 0; k--) {
            if (k >= gN) continue;
            vec4 fl;
            vec4 rm;
            bubbleLayer(k, dir, 1.0e9, fl, rm);
            if (fl.a > 0.01 && Params16.y > 0.001) {
                if (gIsl[k].w > 0.0) {
                    if (!haveL) { portalL = lightSky(dir); haveL = true; }
                    fl.rgb = mix(fl.rgb, portalL, 0.85);
                } else {
                    if (!haveD) { portalD = darkSky(dir); haveD = true; }
                    fl.rgb = mix(fl.rgb, portalD * 1.5 + vec3(0.03, 0.0, 0.07), 0.85);
                }
            }
            res = mix(res, fl.rgb, fl.a);
            res = mix(res, rm.rgb, rm.a);
        }
        fragColor = vec4(clamp(res, 0.0, 1.0), 1.0);
        return;
    }

    float roof = 0.0;
    float aDs = mix(min(aD, 0.20), 1.0, roof);
    float aFs = aF * Params9.w;
    vec3 colS = mix(vec3(0.35, 0.65, 1.00), vec3(1.00, 0.95, 0.85), aL * aL);
    float A = 1.0 - (1.0 - aDs) * (1.0 - aFs);
    vec3 C = colS * aL + MIST_COL * aFs * (1.0 - aDs);

    vec3 hwp = CameraPos + hitRel;
    float gL = 0.0;
    float gD = 0.0;
    for (int k = 0; k < 32; k++) {
        if (k >= gN) break;
        vec4 sb = gIsl[k];
        vec3 axb = gAx[k].xyz;
        if (abs(sb.w) < 1.0 || axb.x < 1.0) continue;
        vec3 qb = (hwp - sb.xyz) / axb;
        float wb = 1.0 - smoothstep(0.80, 1.0, sqrt(dot(qb, qb)));
        if (sb.w > 0.0) gL = max(gL, wb); else gD = max(gD, wb);
    }
    if (roof < 0.5) {
        C += vec3(1.00, 0.90, 0.62) * 0.10 * gL;
        overLayer(C, A, vec3(0.03, 0.00, 0.07), 0.12 * gD);
        C += vec3(0.05, 0.015, 0.11) * 0.5 * gD;
    }

    if (inB && roof < 0.5) {
        float dh = max(dist - 60.0, 0.0);
        float hL = bLm * (1.0 - exp(-dh * Params15.x)) * 0.50;
        float hD = bDm * (1.0 - exp(-dh * Params15.y)) * 0.55;
        overLayer(C, A, vec3(1.00, 0.90, 0.62), hL);
        overLayer(C, A, vec3(1.00, 0.98, 0.90), aBL * 0.55);
        overLayer(C, A, vec3(0.02, 0.00, 0.05), hD);
        overLayer(C, A, vec3(0.03, 0.00, 0.06), aBD * 0.45);

        float sdS = max(dot(dir, SUN_L), 0.0);
        C += vec3(1.00, 0.86, 0.50) * pow(sdS, 6.0) * 0.18 * bLm * Params16.y;
        if (Params16.z > 0.001) {
            C += motes(dir, dist, true) * bLm * Params16.z;
            C += motes(dir, dist, false) * bDm * Params16.z;
        }
    }

    float nearK = smoothstep(8.0, 200.0, dist);
    C *= nearK;
    A *= nearK;
    float Apre = A;

    for (int k = 31; k >= 0; k--) {
        if (k >= gN) continue;
        vec4 fl;
        vec4 rm;
        bubbleLayer(k, dir, dist, fl, rm);
        overLayer(C, A, fl.rgb, fl.a);
        overLayer(C, A, rm.rgb, rm.a);
    }
    if (A > 0.95) { C *= 0.95 / A; A = 0.95; }
    if (any(isnan(C)) || isnan(A)) { C = vec3(0.0); A = 0.0; }
    if (DEBUG_MODE == 2) {
        fragColor = vec4(Apre, A, max(C.r, max(C.g, C.b)), 1.0);
        return;
    }
    fragColor = vec4(C, A);
}
