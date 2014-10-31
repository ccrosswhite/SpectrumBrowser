package gov.nist.spectrumbrowser.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Screen for user to create a new login account
 * @author Julie Kub
 *
 */
class AdminCreateAccount implements SpectrumBrowserCallback {
	
	private VerticalPanel verticalPanel;
	private SpectrumBrowser spectrumBrowser;
	private PasswordTextBox passwordEntry;
	private PasswordTextBox passwordEntryConfirm;
	private TextBox emailEntry;
	private TextBox lastNameEntry;
	private TextBox firstNameEntry;
	private static Logger logger = Logger.getLogger("SpectrumBrowser");
	public static final String LOGIN_LABEL = "Login";
	public HeadingElement helement;
	public HeadingElement welcomeElement;
	
	public AdminCreateAccount(HeadingElement helement, HeadingElement welcomeElement, VerticalPanel verticalPanel, SpectrumBrowser spectrumBrowser) {
		logger.finer("AdminCreateAccount");
		this.verticalPanel = verticalPanel;
		this.spectrumBrowser = spectrumBrowser;
		this.helement = helement;
		this.welcomeElement = welcomeElement;
				
	}
	
	class SubmitNewAccount implements ClickHandler {


			@Override
			public void onClick(ClickEvent event) {
				String firstName = firstNameEntry.getValue();
				String lastName = lastNameEntry.getValue();
				String password = passwordEntry.getValue();
				String passwordConfirm = passwordEntryConfirm.getValue();
				String emailAddress = emailEntry.getValue();
				logger.finer("SubmitNewAccount: " + emailAddress);
				if (firstName == null || firstName.length() == 0) {
					Window.alert("First Name is required.");
					return;
				}
				if (lastName == null || lastName.length() == 0) {
					Window.alert("Last Name is required.");
					return;
				}
				if (password == null || password.length() == 0) {
					Window.alert("Password is required.");
					return;
				}
				if (password != passwordConfirm)
				{
					Window.alert("Password entries must match.");
					return;					
				}
				if (emailAddress == null || emailAddress.length() == 0) {
					Window.alert("Email is required.");
					return;
				}
				//TODO: JEK: look at http://stackoverflow.com/questions/624581/what-is-the-best-java-email-address-validation-method
				// Better to use apache email validator than to use RegEx:
				if (!emailAddress 
						.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
					Window.alert("Please enter a valid email address.");
					return;
				}
				/* The password policy is:			
					At least 14 chars					
					Contains at least one digit					
					Contains at least one lower alpha char and one upper alpha char					
					Contains at least one char within a set of special chars (@#%$^ etc.)					
					Does not contain space, tab, etc. */
				/*
					^                 # start-of-string
					(?=.*[0-9])       # a digit must occur at least once
					(?=.*[a-z])       # a lower case letter must occur at least once
					(?=.*[A-Z])       # an upper case letter must occur at least once
					(?=.*[!@#$%^&+=])  # a special character must occur at least once
					.{12,}             # anything, at least 12 digits
					$                 # end-of-string
				 */

				if (!password 
						.matches("((?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])).{12,}$")) {
					Window.alert("Please enter a password with 1) at least 12 characters, 2) a digit, 3) an upper case letter, 4) a lower case letter, and 5) a special character(!@#$%^&+=).");
					return;
				}	
				if (emailAddress.matches("(.*(\\.gov|\\.mil|\\.GOV|\\.MIL)+)$")){
					//TODO: JEK: if .gov or .mil, automatically create account
					Window.alert(".gov or .mil");
					return;
				}
				else {
					//TODO: JEK: if not .gov/.mil, email admin to approve/deny account creation
					Window.alert("not .gov or .mil");
				}
					
				
			};
		

	}


	public void draw() {
		

		verticalPanel.clear();
		MenuBar menuBar = new MenuBar();
		SafeHtmlBuilder safeHtml = new SafeHtmlBuilder();
		menuBar.addItem(
				safeHtml.appendEscaped(LOGIN_LABEL)
						.toSafeHtml(),
				new Scheduler.ScheduledCommand() {

					@Override
					public void execute() {
						helement.removeAllChildren();
						welcomeElement.removeAllChildren();
						verticalPanel.clear();
						new LoginScreen(spectrumBrowser).draw();

					}
				});
		verticalPanel.add(menuBar);
		HorizontalPanel firstNameField = new HorizontalPanel();
		Label firstNameLabel = new Label("First Name");
		firstNameLabel.setWidth("150px");
		firstNameField.add(firstNameLabel);
		firstNameEntry = new TextBox();
		firstNameEntry.setWidth("150px");
		firstNameEntry.setText("");
		firstNameField.add(firstNameEntry);
		verticalPanel.add(firstNameField);
		
		HorizontalPanel lastNameField = new HorizontalPanel();
		Label lastNameLabel = new Label("Last Name");
		lastNameLabel.setWidth("150px");
		lastNameField.add(lastNameLabel);
		lastNameEntry = new TextBox();
		lastNameEntry.setWidth("150px");
		lastNameEntry.setText("");
		lastNameField.add(lastNameEntry);
		verticalPanel.add(lastNameField);
		
		HorizontalPanel emailField = new HorizontalPanel();
		Label emailLabel = new Label("Email Address");
		emailLabel.setWidth("150px");
		emailField.add(emailLabel);
		emailEntry = new TextBox();
		emailEntry.setWidth("150px");
		emailEntry.setText("");
		emailField.add(emailEntry);
		verticalPanel.add(emailField);
		
		HorizontalPanel passwordField = new HorizontalPanel();
		Label passwordLabel = new Label("Choose a Password (at least 12 chars, uppercase, lowercase, numeric, and special character(!@#$%^&+=))");
		passwordLabel.setWidth("150px");
		passwordField.add(passwordLabel);
		passwordEntry = new PasswordTextBox();
		passwordEntry.setWidth("150px");
		passwordEntry.setText("");
		passwordField.add(passwordEntry);
		verticalPanel.add(passwordField);
		
		HorizontalPanel passwordFieldConfirm = new HorizontalPanel();
		Label passwordLabelConfirm = new Label("Re-type password");
		passwordLabelConfirm.setWidth("150px");
		passwordFieldConfirm.add(passwordLabelConfirm);
		passwordEntryConfirm = new PasswordTextBox();
		passwordEntryConfirm.setWidth("150px");
		passwordEntryConfirm.setText("");
		passwordFieldConfirm.add(passwordEntryConfirm);
		verticalPanel.add(passwordFieldConfirm);
		
		Button buttonNewAccount = new Button("Register");
		buttonNewAccount.addStyleName("sendButton");
		verticalPanel.add(buttonNewAccount);
		buttonNewAccount.addClickHandler( new SubmitNewAccount());
	}

	@Override
	public void onSuccess(Object result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailure(Throwable throwable) {
		// TODO Auto-generated method stub
		
	}

}