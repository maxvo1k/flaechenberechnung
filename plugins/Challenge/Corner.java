import ij.process.ByteProcessor;

class Corner implements Comparable {
	int u;
	int v;
	float q;

	Corner (int u, int v, float q){
		this.u = u;
		this.v = v;
		this.q = q;
	}

	public int compareTo (Object obj) {
		Corner c2 = (Corner) obj;
		if (this.q > c2.q) return -1;
		if (this.q < c2.q) return 1;
		else return 0;
	}

	double dist2 (Corner c2){
		int dx = this.u - c2.u;
		int dy = this.v - c2.v;
		return (dx*dx)+(dy*dy);
	}

	void draw(ByteProcessor ip){
		//draw this corner as a black cross
		int paintvalue = 0; //black
		int size = 4;
		ip.setValue(paintvalue);
		ip.drawLine(u-size,v,u+size,v);
		ip.drawLine(u,v-size,u,v+size);
	}
}