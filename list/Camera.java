public void createPiCamera() {
	try {
		println("createPiCamera START");
		this.piCamera = new RPiCamera("");
		this.piCamera.setWidth(500).setHeight(500) // Set Camera width and height
				.setExposure(Exposure.AUTO) // Set Camera's exposure.
				.setTimeout(2) // Set Camera's timeout.
				.setAddRawBayer(true) // Add Raw Bayer data to image files
				.setHorizontalFlipOn() // Flip orizzontale automatico
				.setRotation(180); // Rotazione immagine automatica
		
		println("createPiCamera END");

	} catch (FailedToRunRaspistillException e) {
		println("ERROR " + e.getMessage());
	}
}

private BufferedImage takePhoto() {
	BufferedImage image = null;
	try {
		println("ACQUISIZIONE FOTO IN CORSO...");
		image = this.piCamera.takeBufferedStill(); 
		println("FOTO ACQUISITA");
	} catch (IOException | InterruptedException e) {
		println("ERROR " + e.getMessage());
	}
	return image;
}

public void connectToSend() throws MqttException {
	this.mqtt.connect(this, this.clientId, this.brokerAddr, this.topic);
}

public void sendMsgMqtt() {
	try {
		BufferedImage bi = this.takePhoto();
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ImageIO.write(bi, "jpg", os);
		String messageBase64 = DatatypeConverter.printBase64Binary(os.toByteArray());
		os.close();
		String messageToSend = "msg( photograph, dispatch, " +  this.getName() + ", " + this.getName().replace("_ctrl", "") + ", " + "ph(\"" + messageBase64 + "\")" + ", " + this.counter++ + ")";
		this.mqtt.publish(this, this.clientId, this.brokerAddr, this.topic, messageToSend, 1, false);
	} catch (Exception e) {
		e.printStackTrace();
	}
}