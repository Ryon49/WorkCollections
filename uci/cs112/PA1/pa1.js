"use strict";

var gl;                 // The webgl context.

var a_coords_loc;       // Location of the a_coords attribute variable in the shader program.
var a_coords_buffer;    // Buffer to hold the values for a_coords.
var a_normal_loc;       // Location of a_normal attribute.
var a_normal_buffer;    // Buffer for a_normal.
var index_buffer;       // Buffer to hold vetex indices from model.

var u_diffuseColor;     // Locations of uniform variables in the shader program
var u_specularColor;
var u_specularExponent;
var u_lightPosition;
var u_modelview;
var u_projection;
var u_normalMatrix;

var projection = mat4.create();          // projection matrix
var modelview;                           // modelview matrix; value comes from rotator
var normalMatrix = mat3.create();        // matrix, derived from model and view matrix, for transforming normal vectors
var rotator;                             // A TrackballRotator to implement rotation by mouse.

var lastTime = 0;
var colors = [  // RGB color arrays for diffuse and specular color values
    [1,1,1],
];

var lightPositions = [  // values for light position
  [0,0,0,1],
];

var objects = [         // Objects for display
    chair(), table(), cube(),
];

var currentModelNumber;  // contains data for the current object

function degToRad(degrees) {
  return degrees * Math.PI / 180;
}


function perspective(out, fovy, aspect, near, far){

    if (document.getElementById("my_gl").checked) {
        let f = 1.0 / Math.tan(fovy / 2), nf;
        out[0] = f / aspect;
        out[5] = f;
        out[11] = -1;

        nf = 1 / (near - far);
        out[10] = (far + near) * nf;
        out[14] = 2 * far * near * nf;
    }
    else {
        mat4.perspective(out, fovy, aspect, near, far);
    }
}

function translate(out, v
    ){

    if (document.getElementById("my_gl").checked) {
        let x = v[0], y = v[1], z = v[2];
        out[12] = out[0] * x + out[4] * y + out[8] * z + out[12];
        out[13] = out[1] * x + out[5] * y + out[9] * z + out[13];
        out[14] = out[2] * x + out[6] * y + out[10] * z + out[14];
        out[15] = out[3] * x + out[7] * y + out[11] * z + out[15];
    }
    else {
        /*
        TODO: Your code goes here.
        use inbuilt_gl functions to perform translation
        */
       mat4.translate(out, out, v)
    }
}

function rotate(out, radius, axis
    ){

    if (document.getElementById("my_gl").checked) {
        let x = axis[0], y = axis[1], z = axis[2];


        let len = Math.hypot(x, y, z);
        x /= len;
        y /= len;
        z /= len;

        let s = Math.sin(radius);
        let c = Math.cos(radius);
        let t = 1 - c;

        let b00 = x * x * t + c;
        let b01 = y * x * t + z * s;
        let b02 = z * x * t - y * s;
        let b10 = x * y * t - z * s;
        let b11 = y * y * t + c;
        let b12 = z * y * t + x * s;
        let b20 = x * z * t + y * s;
        let b21 = y * z * t - x * s;
        let b22 = z * z * t + c;

        let m = out.slice();
        out[0] = m[0] * b00 + m[4] * b01 + m[8] * b02;
        out[1] = m[1] * b00 + m[5] * b01 + m[9] * b02;
        out[2] = m[2] * b00 + m[6] * b01 + m[10] * b02;
        out[3] = m[3] * b00 + m[7] * b01 + m[11] * b02;
        out[4] = m[0] * b10 + m[4] * b11 + m[8] * b12;
        out[5] = m[1] * b10 + m[5] * b11 + m[9] * b12;
        out[6] = m[2] * b10 + m[6] * b11 + m[10] * b12;
        out[7] = m[3] * b10 + m[7] * b11 + m[11] * b12;
        out[8] = m[0] * b20 + m[4] * b21 + m[8] * b22;
        out[9] = m[1] * b20 + m[5] * b21 + m[9] * b22;
        out[10] = m[2] * b20 + m[6] * b21 + m[10] * b22;
        out[11] = m[3] * b20 + m[7] * b21 + m[11] * b22;
    }
    else {
        /*
        TODO: Your code goes here.
        use inbuilt_gl functions to perform rotation
        */
       mat4.rotate(out, out, radius, axis)
    }
}

function scale(out, v
    ){

    if (document.getElementById("my_gl").checked) {
        let x = v[0], y = v[1], z = v[2];
        out[0] *= x;
        out[1] *= x;
        out[2] *= x;
        out[3] *= x;
        out[4] *= y;
        out[5] *= y;
        out[6] *= y;
        out[7] *= y;
        out[8] *= z;
        out[9] *= z;
        out[10] *= z;
        out[11] *= z;
    }
    else {
        /*
        TODO: Your code goes here.
        use inbuilt_gl functions to perform scaling
        */
        mat4.scale(out, out, v)
    }
}

function draw() {
    gl.clearColor(0.15,0.15,0.3,1);
    gl.clear(gl.COLOR_BUFFER_BIT | gl.DEPTH_BUFFER_BIT);

    perspective(projection, Math.PI/5, 1, 10, 20);
    modelview = rotator.getViewMatrix();

    var mv
    currentModelNumber = 0
    
    // draw the 1st chair , object[0]
    installModel(objects[currentModelNumber]);

    /*
    TODO: Your code goes here.

    Compute all the necessary transformation to align object[0] (chair)
    Use your own functions with the proper inputs i.e
        1. translate()
        2. scale()
        3. rotate()
    Apply those transformation to the modelview matrix.
    Not all the transformations are relative and they keep on adding as you modify modelview.
    Hence, you might want to reverse the previous transformation. Keep in mind the order
    in which you apply transformation.
    */


    mv = mat4.clone(modelview)
    rotate(mv, Math.PI / 2, vec3.fromValues(0, 1, 0));
    translate(mv, vec3.fromValues(1.3, -0.6, 0.8))
    update_uniform(mv, projection, currentModelNumber);

    // draw the 2nd chair , object[0]
    installModel(objects[currentModelNumber]);

    mv = mat4.clone(modelview)
    rotate(mv, Math.PI, vec3.fromValues(0, 1, 0));
    translate(mv, vec3.fromValues(1.3, -0.6, 0.6))
    update_uniform(mv, projection, currentModelNumber);


    // draw the 3rd chair , object[0]
    installModel(objects[currentModelNumber]);

    mv = mat4.clone(modelview)
    rotate(mv, -Math.PI / 2, vec3.fromValues(0, 1, 0));
    translate(mv, vec3.fromValues(1.5, -0.6, 0.6))
    update_uniform(mv, projection, currentModelNumber);


    // draw the 4th chair , object[0]
    installModel(objects[currentModelNumber]);

    mv = mat4.clone(modelview)
    translate(mv, vec3.fromValues(1.5, -0.6, 0.8))
    update_uniform(mv, projection, currentModelNumber);


    // draw the Table , object[4]
    currentModelNumber = 1
    installModel(objects[currentModelNumber]);

    mv = mat4.clone(modelview)
    update_uniform(mv,projection, currentModelNumber);


    // draw the Cube , object[2]
    currentModelNumber = 2
    installModel(objects[currentModelNumber]);

    mv = mat4.clone(modelview)
    scale(mv, vec3.fromValues(0.25, 0.25, 0.25))
    translate(mv, vec3.fromValues(0.35, 1.5, 0.35))
    update_uniform(mv,projection, currentModelNumber);

}

/*
  this function assigns the computed values to the uniforms for the model, view and projection
  transform
*/
function update_uniform(modelview,projection,currentModelNumber){

    /* Get the matrix for transforming normal vectors from the modelview matrix,
       and send matrices to the shader program*/
    mat3.normalFromMat4(normalMatrix, modelview);

    gl.uniformMatrix3fv(u_normalMatrix, false, normalMatrix);
    gl.uniformMatrix4fv(u_modelview, false, modelview );
    gl.uniformMatrix4fv(u_projection, false, projection );
    gl.drawElements(gl.TRIANGLES, objects[currentModelNumber].indices.length, gl.UNSIGNED_SHORT, 0);
}



/*
 * Called and data for the model are copied into the appropriate buffers, and the
 * scene is drawn.
 */
function installModel(modelData) {
     gl.bindBuffer(gl.ARRAY_BUFFER, a_coords_buffer);
     gl.bufferData(gl.ARRAY_BUFFER, modelData.vertexPositions, gl.STATIC_DRAW);
     gl.vertexAttribPointer(a_coords_loc, 3, gl.FLOAT, false, 0, 0);
     gl.enableVertexAttribArray(a_coords_loc);
     gl.bindBuffer(gl.ARRAY_BUFFER, a_normal_buffer);
     gl.bufferData(gl.ARRAY_BUFFER, modelData.vertexNormals, gl.STATIC_DRAW);
     gl.vertexAttribPointer(a_normal_loc, 3, gl.FLOAT, false, 0, 0);
     gl.enableVertexAttribArray(a_normal_loc);
     gl.bindBuffer(gl.ELEMENT_ARRAY_BUFFER,index_buffer);
     gl.bufferData(gl.ELEMENT_ARRAY_BUFFER, modelData.indices, gl.STATIC_DRAW);
}


/* Initialize the WebGL context.  Called from init() */
function initGL() {
    var prog = createProgram(gl,"vshader-source","fshader-source");
    gl.useProgram(prog);
    a_coords_loc =  gl.getAttribLocation(prog, "a_coords");
    a_normal_loc =  gl.getAttribLocation(prog, "a_normal");
    u_modelview = gl.getUniformLocation(prog, "modelview");
    u_projection = gl.getUniformLocation(prog, "projection");
    u_normalMatrix =  gl.getUniformLocation(prog, "normalMatrix");
    u_lightPosition=  gl.getUniformLocation(prog, "lightPosition");
    u_diffuseColor =  gl.getUniformLocation(prog, "diffuseColor");
    u_specularColor =  gl.getUniformLocation(prog, "specularColor");
    u_specularExponent = gl.getUniformLocation(prog, "specularExponent");
    a_coords_buffer = gl.createBuffer();
    a_normal_buffer = gl.createBuffer();
    index_buffer = gl.createBuffer();
    gl.enable(gl.DEPTH_TEST);
    gl.uniform3f(u_specularColor, 0.5, 0.5, 0.5);
    gl.uniform4f(u_diffuseColor, 1, 1, 1, 1);
    gl.uniform1f(u_specularExponent, 10);
    gl.uniform4f(u_lightPosition, 0, 0, 0, 1);
}

/* Creates a program for use in the WebGL context gl, and returns the
 * identifier for that program.  If an error occurs while compiling or
 * linking the program, an exception of type String is thrown.  The error
 * string contains the compilation or linking error.  If no error occurs,
 * the program identifier is the return value of the function.
 *    The second and third parameters are the id attributes for <script>
 * elementst that contain the source code for the vertex and fragment
 * shaders.
 */
function createProgram(gl, vertexShaderID, fragmentShaderID) {
    function getTextContent( elementID ) {
            // This nested function retrieves the text content of an
            // element on the web page.  It is used here to get the shader
            // source code from the script elements that contain it.
        var element = document.getElementById(elementID);
        var node = element.firstChild;
        var str = "";
        while (node) {
            if (node.nodeType == 3) // this is a text node
                str += node.textContent;
            node = node.nextSibling;
        }
        return str;
    }
    try {
        var vertexShaderSource = getTextContent( vertexShaderID );
        var fragmentShaderSource = getTextContent( fragmentShaderID );
    }
    catch (e) {
        throw "Error: Could not get shader source code from script elements.";
    }
    var vsh = gl.createShader( gl.VERTEX_SHADER );
    gl.shaderSource(vsh,vertexShaderSource);
    gl.compileShader(vsh);
    if ( ! gl.getShaderParameter(vsh, gl.COMPILE_STATUS) ) {
        throw "Error in vertex shader:  " + gl.getShaderInfoLog(vsh);
     }
    var fsh = gl.createShader( gl.FRAGMENT_SHADER );
    gl.shaderSource(fsh, fragmentShaderSource);
    gl.compileShader(fsh);
    if ( ! gl.getShaderParameter(fsh, gl.COMPILE_STATUS) ) {
       throw "Error in fragment shader:  " + gl.getShaderInfoLog(fsh);
    }
    var prog = gl.createProgram();
    gl.attachShader(prog,vsh);
    gl.attachShader(prog, fsh);
    gl.linkProgram(prog);
    if ( ! gl.getProgramParameter( prog, gl.LINK_STATUS) ) {
       throw "Link error in program:  " + gl.getProgramInfoLog(prog);
    }
    return prog;
}


/**
 * initialization function that will be called when the page has loaded
 */

 function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
function init() {
    try {
        var canvas = document.getElementById("myGLCanvas");
        gl = canvas.getContext("webgl") ||
                         canvas.getContext("experimental-webgl");
        if ( ! gl ) {
            throw "Browser does not support WebGL";
        }
    }
    catch (e) {
        document.getElementById("canvas-holder").innerHTML =
            "<p>Sorry, could not get a WebGL graphics context.</p>";
        return;
    }

    try {
        initGL();  // initialize the WebGL graphics context
    }
    catch (e) {
        document.getElementById("canvas-holder").innerHTML =
            "<p>Sorry, could not initialize the WebGL graphics context:" + e + "</p>";
        return;
    }

    document.getElementById("my_gl").checked = false;
    document.getElementById("my_gl").onchange = draw;
    rotator = new TrackballRotator(canvas, draw, 15);
    draw();
}







