#version 150
in vec2 inPosition; // input from the vertex buffer

uniform mat4 view;
uniform mat4 projection;
uniform float time;

// out vec3  vertColor; //bud ev projektu
out vec3 normal;
out vec3 light;
out vec3 viewDirection;


float getZ(vec2 vec) {
    return sin(time + vec.y * 3.14 *2);
}


vec3 getSphere(vec2 vec) {
    float az = vec.x * 3.14;
    float ze = vec.y * 3.14 / 2;
    float r = 1;

    float x = r*cos(az)*cos(ze);
    float y = 2* r*sin(az)*cos(ze); //2pryc
    float z = 0.5 * r*sin(ze); //0.5pryc

    return vec3(x,y,z);
}

vec3 getSphereNormal(vec2 vec){
    vec3 u = getSphere(vec+vec2(0.001, 0))
                - getSphere(vec-vec2(0.001,0));

    vec3 v = getSphere(vec+vec2(0, 0.001))
                 - getSphere(vec-vec2(0, 0.001));

    return cross(u,v); //vektorovy soucin
}

void main() {

    vec2 position;
    position = inPosition * 2 - 1;
  //  vec4 pos4 = vec4(position, getZ(position), 1.0);
   vec4 pos4 = vec4(getSphere(position),1.0);
	gl_Position = projection * view * pos4;
	//vec4(position, getZ(position) , 1.0);

// vercol v projektu bude
    // vertColor = pos4.xyz;

    normal = mat3(view)* getSphereNormal(position);

    vec3 lightPos = vec3(1,1,0);
    light = lightPos - (view * pos4).xyz;

    viewDirection = - pos4.xyz;

} 
