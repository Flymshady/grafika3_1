#version 150

out vec4 outColor; // output from the fragment shader
in vec3 vertColor;


void main() {
	outColor = vec4(vertColor, 1.0);
} 
