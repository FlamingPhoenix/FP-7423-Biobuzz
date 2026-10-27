package org.firstinspires.ftc.teamcode.ai;

import java.util.HashMap;
import java.util.Map;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

public class OrtTest {
    public void main() throws OrtException {

        OrtEnvironment env = OrtEnvironment.getEnvironment();
        OrtSession session = env.createSession("timesTwoTest.onnx", new OrtSession.SessionOptions());
        float[] source = new float[] {1,2,3};
        OnnxTensor t = OnnxTensor.createTensor(env, source);
        Map<String, OnnxTensor> inputs = new HashMap<>();
        inputs.put("input_tensor", t);
        try (OrtSession.Result result = session.run(inputs);){
            float[] output = (float[]) result.get(0).getValue();
        }
    }
}
