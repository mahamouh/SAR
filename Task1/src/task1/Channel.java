package task1;

import exception.DisconnectedException;
import task1Interface.IChannel;

public class Channel implements IChannel {
	CircularBuffer bufferOut;
	CircularBuffer bufferIn;
	boolean isDisconnect = false;
	Channel rmChannel;

	public Channel(CircularBuffer out, CircularBuffer in, Channel rmChannel) {
		this.bufferOut = out;
		this.bufferIn = in; 
		this.rmChannel = rmChannel;
	}
 
	@Override
	public int read(byte[] bytes, int offset, int length) throws DisconnectedException {
		int cpt = 0; // Nb des bytes lus
		if (disconnected()) {
			throw new DisconnectedException("La connection a été coupée.");
		}
		while (!bufferIn.empty() || cpt != length - offset) {
			if (!disconnected()) {
				bytes[offset + cpt] = bufferIn.pull();
				cpt += 1;
			} else {
				throw new DisconnectedException("La connection a été coupée.");
			}
		}
		System.out.println("Nombre d'octetcs envoyé: " + (length - offset));
		System.out.println("Nombre d'octetcs lus: " + cpt);
		return cpt;
	}

	@Override
	public int write(byte[] bytes, int offset, int length) throws DisconnectedException {
		int cpt = 0; // Nb des bytes écrits
		if (disconnected()) {
			throw new DisconnectedException("La connection a été coupée. Veuillez vous déconnecter");
		}
		while (!bufferIn.full() || cpt != length - offset) {
			if (!disconnected()) {
				bufferOut.push(bytes[offset + cpt]);
				cpt += 1;
			} else {
				throw new DisconnectedException("La connection a été coupée. Veuillez vous déconnecter");
			}
		}
		System.out.println("Nombre d'octetcs envoyé: " + (length - offset));
		System.out.println("Nombre d'octetcs écrits: " + cpt);
		return cpt;
	}

	@Override
	public void disconnect() {
		System.out.println("Demande de deconnexion réalisée");
		this.isDisconnect = true;
		this.rmChannel.isDisconnect = true;
	}

	@Override
	public boolean disconnected() {
		return isDisconnect;
	}

}
