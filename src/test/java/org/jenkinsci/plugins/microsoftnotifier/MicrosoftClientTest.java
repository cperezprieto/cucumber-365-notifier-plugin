package org.jenkinsci.plugins.microsoftnotifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.jenkinsci.plugins.microsoftnotifier.CucumberResult;
import org.jenkinsci.plugins.microsoftnotifier.FeatureResult;
import org.jenkinsci.plugins.microsoftnotifier.MicrosoftClient;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;

public class MicrosoftClientTest {

	@Test
	public void canGenerateFullSuccessfulMicrosoftkMessage() throws FileNotFoundException {
		JsonElement element = loadTestResultFile("pending-result.json");
		assertNotNull(element);
		CucumberResult result = new MicrosoftClient("https://jenkins.seosautomation.com/job/try_and_buy_suite_sage300_qa/20/", "https://jenkins.seosautomation.com/", false).processResults(element);
		assertNotNull(result);
		assertNotNull(result.getFeatureResults());
		assertEquals(8, result.totalScenarios);
		assertEquals(7, result.totalPassedTests);
		assertEquals(0, result.totalFailedTests);
		assertEquals(1, result.totalPendingTests);
		assertEquals(100, result.passPercentage);
		
		String microsoftMessage = result.toMicrosoftMessage("test_job_ASD(A*A\\A).COM", 7, "https://jenkins.seosautomation.com/", null, "user c_perez_prieto", "35 minutos");
		assertNotNull(microsoftMessage);
		assertEquals(true, microsoftMessage.contains("user c\\\\_perez\\\\_prieto"));
	}
/*
	@Test
	public void canGenerateMinimalSuccessfulMicrosoftMessage() throws FileNotFoundException {
		JsonElement element = loadTestResultFile("successful-result.json");
		assertNotNull(element);
		CucumberResult result = new MicrosoftClient("http://microsoft.com/", "http://jenkins:8080/", true).processResults(element);
		assertNotNull(result);
		assertNotNull(result.getFeatureResults());
		assertEquals(8, result.getTotalScenarios());
		assertEquals(0, result.getTotalFeatures());
		assertEquals(100, result.getPassPercentage());

		String microsoftMessage = result.toMicrosoftMessage("test-job", 7, "http://jenkins:8080/", null);
		assertNotNull(microsoftMessage);
	}
	
	@Test
	public void canGenerateFullFailedMicrosoftMessage() throws FileNotFoundException {
		JsonElement element = loadTestResultFile("failed-result.json");
		assertNotNull(element);
		CucumberResult result = new MicrosoftClient("http://microsoft.com/", "http://jenkins:8080/", false).processResults(element);
		assertNotNull(result);
		assertNotNull(result.getFeatureResults());
		assertEquals(8, result.getTotalScenarios());
		assertEquals(8, result.getTotalFeatures());
		assertEquals(87, result.getPassPercentage());
	}
	
	@Test
	public void canGeneratePendingMicrosoftMessage() throws FileNotFoundException {
		JsonElement element = loadTestResultFile("pending-result.json");
		assertNotNull(element);
		CucumberResult result = new MicrosoftClient("http://microsoft.com/", "http://jenkins:8080/", false).processResults(element);
		assertNotNull(result);
		assertNotNull(result.getFeatureResults());
		assertEquals(8, result.getTotalScenarios());
		assertEquals(8, result.getTotalFeatures());
		assertEquals(100, result.getPassPercentage());
		assertEquals(1, result.getPendingTests());
	}

	@Test
	public void canGenerateMinimalFailedMicrosoftMessage() throws FileNotFoundException {
		JsonElement element = loadTestResultFile("failed-result.json");
		assertNotNull(element);
		CucumberResult result = new MicrosoftClient("http://microsoft.com/", "http://jenkins:8080/", true).processResults(element);
		assertNotNull(result);
		assertNotNull(result.getFeatureResults());
		assertEquals(8, result.getTotalScenarios());
		assertEquals(1, result.getTotalFeatures());
		assertEquals(87, result.getPassPercentage());
	}
	
	@Test
	public void canGenerateGoodMessage() {
		String microsoftMessage = successfulResult().toMicrosoftMessage("test-job", 1, "http://jenkins:8080/", null);
		assertNotNull(microsoftMessage);
		assertTrue(microsoftMessage.contains("good"));
	}

	@Test
	public void canGenerateMarginalMessage() {
		String microsoftMessage = marginalResult().toMicrosoftMessage("test-job", 1, "http://jenkins:8080/", null);
		assertNotNull(microsoftMessage);
		assertTrue(microsoftMessage.contains("warning"));
	}

	@Test
	public void canGenerateBadMessage() {
		String microsoftMessage = badResult().toMicrosoftMessage("test-job", 1, "http://jenkins:8080/", null);
		assertNotNull(microsoftMessage);
		assertTrue(microsoftMessage.contains("danger"));
	}
	
	@Test
	public void canGeneratePendingMessage() {
		String microsoftMessage = pendingResult().toMicrosoftMessage("test-job", 1, "http://jenkins:8080/", null);
		assertNotNull(microsoftMessage);
		assertTrue(microsoftMessage.contains("warning"));
	}
*/
	private JsonElement loadTestResultFile(String filename) throws FileNotFoundException {
		File result = new File("src/test/resources", filename);
		assertNotNull(result);
		assertTrue(result.exists());
		return getResultFileAsJsonElement(new FileInputStream(result));
	}

	private JsonElement getResultFileAsJsonElement(InputStream stream) {
		final Gson gson = new Gson();
		final JsonReader jsonReader = new JsonReader(new InputStreamReader(stream));
		return gson.fromJson(jsonReader, JsonElement.class);
	}
}
