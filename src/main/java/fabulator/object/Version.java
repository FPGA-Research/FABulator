package fabulator.object;

import lombok.Getter;

@Getter
public class Version {

    private int major;
    private int minor;
    private int patch;

    public Version(String versionString) throws NumberFormatException {
        this.parseString(versionString);
    }

    private void parseString(String versionString) throws NumberFormatException {
        if (versionString != null && !versionString.isEmpty()) {
            String[] parts = versionString.split("\\.");

            if (parts.length == 3) {
                this.major = Integer.parseInt(parts[0]);
                this.minor = Integer.parseInt(parts[1]);
                this.patch = Integer.parseInt(parts[2]);
            } else {
                throw new NumberFormatException();
            }
        } else {
            throw new NumberFormatException();
        }
    }

    public static boolean outdated(Version minVersion, Version version) {
        boolean outdated = true;

        if (version != null) {
            outdated = !minVersion.lessEqual(version);
        }
        return outdated;
    }

    public boolean lessEqual(Version version) {
        boolean lessEqual = false;

        if (version.getMajor() > this.major) {
            lessEqual = true;

        } else if (version.getMajor() == this.major) {
            if (version.getMinor() > this.minor) {
                lessEqual = true;

            } else if (version.getMinor() == this.minor) {
                lessEqual = version.getPatch() >= this.patch;
            }
        }
        return lessEqual;
    }

    @Override
    public String toString() {
        return String.format(
                "%d.%d.%d",
                this.major,
                this.minor,
                this.patch
        );
    }
}
