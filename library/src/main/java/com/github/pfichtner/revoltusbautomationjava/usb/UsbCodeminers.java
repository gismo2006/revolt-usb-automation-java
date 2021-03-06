package com.github.pfichtner.revoltusbautomationjava.usb;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.codeminders.hidapi.ClassPathLibraryLoader;
import com.codeminders.hidapi.HIDDevice;
import com.codeminders.hidapi.HIDDeviceNotFoundException;
import com.codeminders.hidapi.HIDManager;

public class UsbCodeminers implements Usb, Closeable {

	static {
		ClassPathLibraryLoader.loadNativeHIDLibrary();
	}

	private int vendorId;

	private int productId;

	private HIDDevice device;

	private UsbCodeminers(short vendorId, short productId) {
		this.vendorId = toUint32(vendorId);
		this.productId = toUint32(productId);
	}

	private static int toUint32(short shortVal) {
		return shortVal & 0xFFFF;
	}

	private String noSerialNumber() {
		return null;
	}

	public static UsbCodeminers newInstance(short vendorId, short productId) {
		return new UsbCodeminers(vendorId, productId);
	}

	public void setInterfaceNum(int intValue) {
		// do nothing
	}

	public void setOutEndpoint(byte byteValue) {
		// do nothing
	}

	public void setTimeout(TimeUnit timeUnit, long longValue) {
		// do nothing
	}

	public void connect() throws IOException {
		HIDManager hidManager = HIDManager.getInstance();
		this.device = fixCodeminersBugCheckForNullArray(hidManager).openById(
				this.vendorId, this.productId, noSerialNumber());
	}

	private HIDManager fixCodeminersBugCheckForNullArray(HIDManager hidManager)
			throws IOException, HIDDeviceNotFoundException {
		if (hidManager.listDevices() == null) {
			throw new HIDDeviceNotFoundException();
		}
		return hidManager;
	}

	public void close() throws IOException {
		this.device.close();
		HIDManager.getInstance().release();
	}

	public void write(byte[] data) throws IOException {
		this.device.write(data);
	}

	public boolean hasHotplug() {
		// https://github.com/baalhiverne/judraw/blob/master/judrawlib/src/org/judraw/judrawlib/RawDevice.java
		return false;
	}

	public void registerCallback(UsbHotPlugEventListener hotPlugEventListener) {
		throw new UnsupportedOperationException("hotplug not supported");
	}

	public static boolean dependenciesOnClasspath() {
		return Reflections.existsClass(
				"com.codeminders.hidapi.ClassPathLibraryLoader",
				UsbCodeminers.class.getClassLoader());
	}

}
