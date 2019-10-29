#version 150
in vec2 inPosition; // input from the vertex buffer

uniform mat4 view;
uniform mat4 projection;
uniform float time;





// udelat taj dalsi objekt
vec3 getSphere(vec2 vec) {
    float az = vec.x * 3.14;
    float ze = vec.y * 3.14 / 2;
    float r = 1;

    float x = r*cos(az)*cos(ze);
    float y = 2* r*sin(az)*cos(ze); //2pryc
    float z = 0.5 * r*sin(ze); //0.5pryc

    return vec3(x,y,z);
}


void main() {
//metoda na rozvetveni objektu pres type == 1 ...
    vec2 position;
    position = inPosition * 2 - 1;
  //  vec4 pos4 = vec4(position, getZ(position), 1.0);
   vec4 pos4 = vec4(getSphere(position),1.0);
	gl_Position = projection * view * pos4;

} 
