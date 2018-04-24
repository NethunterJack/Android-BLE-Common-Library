package no.nordicsemi.android.ble.common.callback;

import android.bluetooth.BluetoothDevice;
import android.support.annotation.NonNull;

import org.junit.Test;

import no.nordicsemi.android.ble.callback.DataCallback;
import no.nordicsemi.android.ble.data.Data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("ConstantConditions")
public class CGMSpecificOpsControlPointDataCallbackTest {
	private boolean success;
	private boolean invalidCrc;
	private boolean invalidData;
	private boolean valueReceived;
	private boolean secured;
	private int error;
	private int interval;
	private float patientHighAlertLevel;
	private float patientLowAlertLevel;
	private float hypoAlertLevel;
	private float hyperAlertLevel;
	private float rateOfDecreaseAlertLevel;
	private float rateOfIncreaseAlertLevel;

	private final DataCallback callback = new CGMSpecificOpsControlPointDataCallback() {
		@Override
		public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
			CGMSpecificOpsControlPointDataCallbackTest.this.success = false;
			CGMSpecificOpsControlPointDataCallbackTest.this.invalidCrc = false;
			CGMSpecificOpsControlPointDataCallbackTest.this.invalidData = false;
			CGMSpecificOpsControlPointDataCallbackTest.this.valueReceived = false;
			CGMSpecificOpsControlPointDataCallbackTest.this.error = 0;
			CGMSpecificOpsControlPointDataCallbackTest.this.interval = 0;
			CGMSpecificOpsControlPointDataCallbackTest.this.patientHighAlertLevel = 0.0f;
			CGMSpecificOpsControlPointDataCallbackTest.this.patientLowAlertLevel = 0.0f;
			CGMSpecificOpsControlPointDataCallbackTest.this.hypoAlertLevel = 0.0f;
			CGMSpecificOpsControlPointDataCallbackTest.this.hyperAlertLevel = 0.0f;
			CGMSpecificOpsControlPointDataCallbackTest.this.rateOfDecreaseAlertLevel = 0.0f;
			CGMSpecificOpsControlPointDataCallbackTest.this.rateOfIncreaseAlertLevel = 0.0f;
			super.onDataReceived(device, data);
		}

		@Override
		public void onCGMSpecificOpsOperationCompleted() {
			CGMSpecificOpsControlPointDataCallbackTest.this.success = true;
		}

		@Override
		public void onCGMSpecificOpsOperationError(final int error) {
			CGMSpecificOpsControlPointDataCallbackTest.this.error = error;
		}

		@Override
		public void onCGMSpecificOpsResponseReceivedWithCrcError(@NonNull final Data data) {
			CGMSpecificOpsControlPointDataCallbackTest.this.invalidCrc = true;
		}

		@Override
		public void onContinuousGlucoseCommunicationIntervalReceived(final int interval, final boolean secured) {
			CGMSpecificOpsControlPointDataCallbackTest.this.interval = interval;
			CGMSpecificOpsControlPointDataCallbackTest.this.secured = secured;
		}

		@Override
		public void onContinuousGlucoseCalibrationValueReceived(final float glucoseConcentrationOfCalibration,
																final int calibrationTime, final int nextCalibrationTime,
																final int type, final int sampleLocation,
																final int calibrationDataRecordNumber,
																final CGMCalibrationStatus status, final boolean secured) {
			CGMSpecificOpsControlPointDataCallbackTest.this.valueReceived = true;
			assertEquals("Glucose concentration of calibration", 100.0f, glucoseConcentrationOfCalibration, 0.01);
			assertEquals("Calibration time", 10, calibrationTime);
			assertEquals("Calibration time", 20, nextCalibrationTime);
			assertEquals("Type", TYPE_CAPILLARY_PLASMA, type);
			assertEquals("Sample location", SAMPLE_LOCATION_EARLOBE, sampleLocation);
			assertEquals("Data record Number", 1, calibrationDataRecordNumber);
			assertNotNull(status);
			assertFalse(status.rejected);
			assertFalse(status.dataOutOfRange);
			assertTrue(status.processPending);
			CGMSpecificOpsControlPointDataCallbackTest.this.secured = secured;
		}

		@Override
		public void onContinuousGlucosePatientHighAlertReceived(final float alertLevel, final boolean secured) {
			CGMSpecificOpsControlPointDataCallbackTest.this.patientHighAlertLevel = alertLevel;
			CGMSpecificOpsControlPointDataCallbackTest.this.secured = secured;
		}

		@Override
		public void onContinuousGlucosePatientLowAlertReceived(final float alertLevel, final boolean secured) {
			CGMSpecificOpsControlPointDataCallbackTest.this.patientLowAlertLevel = alertLevel;
			CGMSpecificOpsControlPointDataCallbackTest.this.secured = secured;
		}

		@Override
		public void onContinuousGlucoseHypoAlertReceived(final float alertLevel, final boolean secured) {
			CGMSpecificOpsControlPointDataCallbackTest.this.hypoAlertLevel = alertLevel;
			CGMSpecificOpsControlPointDataCallbackTest.this.secured = secured;
		}

		@Override
		public void onContinuousGlucoseHyperAlertReceived(final float alertLevel, final boolean secured) {
			CGMSpecificOpsControlPointDataCallbackTest.this.hyperAlertLevel = alertLevel;
			CGMSpecificOpsControlPointDataCallbackTest.this.secured = secured;
		}

		@Override
		public void onContinuousGlucoseRateOfDecreaseAlertReceived(final float alertLevel, final boolean secured) {
			CGMSpecificOpsControlPointDataCallbackTest.this.rateOfDecreaseAlertLevel = alertLevel;
			CGMSpecificOpsControlPointDataCallbackTest.this.secured = secured;
		}

		@Override
		public void onContinuousGlucoseRateOfIncreaseAlertReceived(final float alertLevel, final boolean secured) {
			CGMSpecificOpsControlPointDataCallbackTest.this.rateOfIncreaseAlertLevel = alertLevel;
			CGMSpecificOpsControlPointDataCallbackTest.this.secured = secured;
		}

		@Override
		public void onInvalidDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
			CGMSpecificOpsControlPointDataCallbackTest.this.invalidData = true;
		}
	};

	@Test
	public void onCGMSpecificOpsOperationCompleted() {
		final Data data = new Data(new byte[] { 28, 2, 1});
		callback.onDataReceived(null, data);
		assertTrue(success);
	}

	@Test
	public void onCGMSpecificOpsOperationError() {
		final Data data = new Data(new byte[] { 28, 2, 2});
		callback.onDataReceived(null, data);
		assertEquals(error, 2);
	}

	@Test
	public void onCGMSpecificOpsOperationCompleted_withCrc() {
		final Data data = new Data(new byte[] { 28, 2, 1, (byte) 0x3C, (byte) 0x3B});
		callback.onDataReceived(null, data);
		assertTrue(success);
	}

	@Test
	public void onCGMSpecificOpsOperationError_withCrc() {
		final Data data = new Data(new byte[] { 28, 2, 2, (byte) 0xA7, (byte) 0x09});
		callback.onDataReceived(null, data);
		assertEquals(error, 2);
	}

	@Test
	public void onCGMSpecificOpsResponseReceivedWithCrcError() {
		final Data data = new Data(new byte[] { 28, 2, 2, (byte) 0xA7, (byte) 0x01});
		callback.onDataReceived(null, data);
		assertTrue(invalidCrc);
	}

	@Test
	public void onContinuousGlucoseCalibrationValueReceived() {
		final Data data = new Data(new byte[11]);
		data.setValue(6, Data.FORMAT_UINT8, 0);
		data.setValue(1, 2, Data.FORMAT_SFLOAT, 1);
		data.setValue(10, Data.FORMAT_UINT16, 3);
		data.setValue(0x32, Data.FORMAT_UINT8, 5);
		data.setValue(20, Data.FORMAT_UINT16, 6);
		data.setValue(1, Data.FORMAT_UINT16, 8);
		data.setValue(0b100, Data.FORMAT_UINT8, 10);

		callback.onDataReceived(null, data);
		assertTrue(valueReceived);
		assertFalse(secured);
	}

	@Test
	public void onContinuousGlucoseCalibrationValueReceived_withCrc() {
		final Data data = new Data(new byte[13]);
		data.setValue(6, Data.FORMAT_UINT8, 0);
		data.setValue(1, 2, Data.FORMAT_SFLOAT, 1);
		data.setValue(10, Data.FORMAT_UINT16, 3);
		data.setValue(0x32, Data.FORMAT_UINT8, 5);
		data.setValue(20, Data.FORMAT_UINT16, 6);
		data.setValue(1, Data.FORMAT_UINT16, 8);
		data.setValue(0b100, Data.FORMAT_UINT8, 10);
		data.setValue(0xB2BF, Data.FORMAT_UINT16, 11);

		callback.onDataReceived(null, data);
		assertTrue(valueReceived);
		assertTrue(secured);
	}

	@Test
	public void onContinuousGlucosePatientHighAlertReceived() {
		final Data data = new Data(new byte[] { 9, 12, -16});
		callback.onDataReceived(null, data);
		assertEquals("Level", 1.2f, patientHighAlertLevel, 0.01);
		assertFalse(secured);
	}

	@Test
	public void onContinuousGlucosePatientLowAlertReceived() {
		final Data data = new Data(new byte[] { 12, 11, (byte) 0b111000000, (byte) 0x34, (byte) 0xBE});
		callback.onDataReceived(null, data);
		assertEquals("Level", 0.0011f, patientLowAlertLevel, 0.0001);
		assertTrue(secured);
	}

	@Test
	public void onContinuousGlucoseHypoAlertReceived() {
		final Data data = new Data(new byte[] { 15, 1, 0});
		callback.onDataReceived(null, data);
		assertEquals("Level", 1.0f, hypoAlertLevel, 0.00);
		assertFalse(secured);
	}

	@Test
	public void onContinuousGlucoseHyperAlertReceived() {
		final Data data = new Data(new byte[] { 18, 10, 32});
		callback.onDataReceived(null, data);
		assertEquals("Level", 1000f, hyperAlertLevel, 0.01);
		assertFalse(secured);
	}

	@Test
	public void onContinuousGlucoseRateOfDecreaseAlertReceived() {
		final Data data = new Data(new byte[] { 21, 1, 16});
		callback.onDataReceived(null, data);
		assertEquals("Level", 10.0f, rateOfDecreaseAlertLevel, 0.00);
		assertFalse(secured);
	}

	@Test
	public void onContinuousGlucoseRateOfIncreaseAlertReceived() {
		final Data data = new Data(new byte[] { 24, 10, 64});
		callback.onDataReceived(null, data);
		assertEquals("Level", 100000f, rateOfIncreaseAlertLevel, 0.01);
		assertFalse(secured);
	}

	@Test
	public void onContinuousGlucoseCommunicationIntervalReceived() {
		final Data data = new Data(new byte[] { 3, 10, (byte) 0x8A, (byte) 0x75});
		callback.onDataReceived(null, data);
		assertEquals("Interval", 10, interval);
		assertTrue(secured);
	}

	@Test
	public void onInvalidDataReceived() {
		final Data data = new Data(new byte[] { 6, 10, 1 });
		callback.onDataReceived(null, data);
		assertTrue(invalidData);
	}
}