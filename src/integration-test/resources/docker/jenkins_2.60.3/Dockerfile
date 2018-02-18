FROM jenkins/jenkins:2.60.3
#to override any file: copy to /usr/share/jenkins/ref/.*override, where .* - file name
COPY config.xml /usr/share/jenkins/ref/config.xml.override
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
#install jenkins plugin during image preparation
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt
#disable setup wizard (asking pass from file and admin user creation)
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
USER jenkins