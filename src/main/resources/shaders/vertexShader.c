
// <max_uniform_mat4_array_len> fills up all available uniform space, 
// so we have to divide it by 2 to save some for additional variables
#define BATCH_SIZE (<max_uniform_mat4_array_len> / 2)
uniform     vec4    uniform_ColorScale[BATCH_SIZE];
uniform     mat4    uniform_Transformation[BATCH_SIZE];
in 	        vec4  	attribute_Position;
in 	        vec4  	attribute_Color;
out 	    vec4    vertexColor;

void main(void) {
	vertexColor = uniform_ColorScale[gl_DrawIDARB] * attribute_Color;
	gl_Position = uniform_Transformation[gl_DrawIDARB] * attribute_Position;
}
