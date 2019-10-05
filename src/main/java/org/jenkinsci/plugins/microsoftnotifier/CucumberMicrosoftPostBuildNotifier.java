package org.jenkinsci.plugins.microsoftnotifier;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

public class CucumberMicrosoftPostBuildNotifier extends Recorder {

	private static final Logger LOG = Logger.getLogger(CucumberMicrosoftPostBuildNotifier.class.getName());

	private final String jobWebhook;
	private final String json;
	private final boolean hideSuccessfulResults;

	@DataBoundConstructor
	public CucumberMicrosoftPostBuildNotifier(String jobWebhook, String json, boolean hideSuccessfulResults) {
		this.jobWebhook = jobWebhook;
		this.json = json;
		this.hideSuccessfulResults = hideSuccessfulResults;
	}

	public String getJobWebhook() {
		return jobWebhook;
	}

	public String getJson() {
		return json;
	}

	public boolean getHideSuccessfulResults() {
		return hideSuccessfulResults;
	}

	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
			throws InterruptedException, IOException {
		String webhookUrl = CucumberMicrosoft.get().getWebHookEndpoint();

		if (this.jobWebhook != null && this.jobWebhook != "")
			webhookUrl = this.jobWebhook;

		if (StringUtils.isEmpty(webhookUrl)) {
			listener.getLogger().println("No webhook found... Skipping cucumber 365 notifier.");
			return true;
		}

		try {
			CucumberMicrosoftService service = new CucumberMicrosoftService(webhookUrl);
			service.sendCucumberReportToMicrosoft(build, build.getWorkspace(), json, null, hideSuccessfulResults);
		} catch (Exception e) {
			LOG.severe(e.getMessage());
			listener.getLogger().println("[Cucumber365Notifier] Unable to send message to 365");
		}

		return true;
	}

	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}

	@Extension
	public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

		private String webHookEndpoint;
	
		public DescriptorImpl() {
			load();
		}

		public FormValidation doCheckJobWebhook(@QueryParameter String value) throws IOException, ServletException {
			if (value.length() == 0)
				return FormValidation.warning("It will use the default Webhook");
			if (value.length() < 4)
				return FormValidation.warning("Isn't the uri too short?");
			if (!value.startsWith("https://outlook.office.com/webhook/")) {
				return FormValidation.warning("365 endpoint should start with https://outlook.office.com/webhook/");
			}
			return FormValidation.ok();
		}

		public boolean isApplicable(Class<? extends AbstractProject> aClass) {
			return true;
		}

		public String getDisplayName() {
			return "Send Cucumber Report to 365";
		}

		@Override
		public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
			webHookEndpoint = formData.getString("webHookEndpoint");
			save();
			return super.configure(req, formData);
		}

		public String getWebHookEndpoint() {
			return webHookEndpoint;
		}
	}

	public BuildStepMonitor getRequiredMonitorService() {
		return BuildStepMonitor.STEP;
	}
}
