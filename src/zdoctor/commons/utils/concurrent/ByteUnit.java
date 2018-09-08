package zdoctor.commons.utils.concurrent;

public enum ByteUnit {
	BYTE {

		@Override
		long baseUnit() {
			return BYTEUNIT;
		}

	},

	KILOBYTE {

		@Override
		long baseUnit() {
			return KILOBYTEUNIT;
		}

	},

	MEGABYTE {

		@Override
		long baseUnit() {
			return MEGABYTEUNIT;
		}

	},

	GIGABYTE {

		@Override
		long baseUnit() {
			return GIGABYTEUNIT;
		}

	},

	TERABYTE {

		@Override
		long baseUnit() {
			return TERABYTEUNIT;
		}

	},

	PETABYTE {

		@Override
		long baseUnit() {
			return PETABYTEUNIT;
		}

	},

	EXABYTE {

		@Override
		long baseUnit() {
			return EXABYTEUNIT;
		}

	};

	static final long BYTEUNIT = 0;
	static final long KILOBYTEUNIT = 1;
	static final long MEGABYTEUNIT = 2;
	static final long GIGABYTEUNIT = 3;
	static final long TERABYTEUNIT = 4;
	static final long PETABYTEUNIT = 5;
	static final long EXABYTEUNIT = 6;

	abstract long baseUnit();

	public double convert(double sourceSize, ByteUnit sourceUnit) {
		if (baseUnit() > sourceUnit.baseUnit()) {
			return sourceSize / Math.pow(1024d, baseUnit());
		} else {
			return sourceSize * Math.pow(1024d, sourceUnit.baseUnit());
		}

	}

}
