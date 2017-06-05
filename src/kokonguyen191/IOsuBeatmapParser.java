package kokonguyen191;

import java.util.ArrayList;

/**
 * 
 * @author Koko
 *
 */
public interface IOsuBeatmapParser {

	/**
	 * 
	 * @return list of offsets of notes
	 */
	public ArrayList<Integer> getBeats();

	// =========================================================================
	// All methods below work with timing points and return lists of same length
	// =========================================================================

	/**
	 * 
	 * @return list of offsets of timing points
	 */
	public ArrayList<Double> getOffsets();

	/**
	 * 
	 * @return list of ms/beat of timing points, bpm = 60000 / mspb
	 */
	public ArrayList<Double> getMsPerBeat();

	/**
	 * 
	 * @return list of inheritance, true/1 is inherited, false/0 is uninherited
	 */
	public ArrayList<Boolean> getInheritance();

	/**
	 * 
	 * @return list of sound samples
	 */
	public ArrayList<Integer> getSounds();

	/**
	 * 
	 * @return list of sound volumes
	 */
	public ArrayList<Integer> getVolume();

	/**
	 * 
	 * @return list of kiai modes
	 */
	public ArrayList<Integer> getKiai();
}
