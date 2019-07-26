package acw.setm.test;

import java.io.File;
import java.io.IOException;

import acw.common.utils.file.FileExistenceUtils;
import acw.setm.launcher.SETMLauncher;
import acw.setm.param.SETMCmdOption;

public class SETMTest {

	public static void main(String[] args) throws IOException{
		String dpDataRoot = "/Users/wu-chuan/PROJ.201704.ESD/data.esd.NYT-Sal";
		String dpTrain = dpDataRoot + File.separator + "ds.ESD-NYT.sample-train";
		String dpTest = dpDataRoot + File.separator + "ds.ESD-NYT.sample-test";
		String dpOut = "/Users/wu-chuan/tmp";
		
		SETMCmdOption option = new SETMCmdOption();
		option.niters = 10;
		
		option.dpTrain = dpOut + File.separator + "setm.train";
		option.dpTest = dpOut + File.separator + "setm.test";
		FileExistenceUtils.createDirIfNotExists(option.dpTrain);
		FileExistenceUtils.createDirIfNotExists(option.dpTest);
		
		option.fpWTrn = dpTrain + File.separator + "nyt-esd-train-words-sample";
		option.fpSETrn = dpTrain + File.separator + "nyt-esd-train-salient-entities-sample";
		option.fpOETrn = dpTrain + File.separator + "nyt-esd-train-all-entities-sample";
		option.fpWInf = dpTest + File.separator + "nyt-esd-test-words-sample";
		option.fpSEInf = dpTest + File.separator + "nyt-esd-test-null-salient-entities-sample";
		option.fpOEInf = dpTest + File.separator + "nyt-esd-test-all-entities-sample";
		
//		SETMLauncher.estimate(option);
		SETMLauncher.inference(option);
	}
}
