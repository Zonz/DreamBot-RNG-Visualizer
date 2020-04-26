# DreamBot RNG Visualizer
This is a mostly useless utility script for [DreamBot](https://dreambot.org/) to visualize the distribution of various random number generators.
Inspired by [this forum thread](https://dreambot.org/forums/index.php?/topic/18858-weighted-random-numbers/). (Gaussian algorithms taken from that thread, with some cleaning and a couple bug fixes)

X-Axis of the graph is each potentially generated point.
Y-Axis is the relative probability of being generated.
Points higher on the y-axis have a higher probability of being generated than other points.

Green points are points that tie for the highest probability.
Orange points are *generated* points that tie for the lowest probability.
Red points are points that haven't been generated at all.

Yellow line is an average of the surrounding generated points.
Blue line is a bezier curve using the averages of the generated points as control points.

---
### Contains visualizations for the following algorithms:
<details>
  <Summary>Standard (<a href="https://docs.oracle.com/javase/8/docs/api/java/util/Random.html#nextInt--">random.nextInt()</a>)</Summary>
  
  ![Standard 300]
  ![Standard 20]
</details>
<details>
  <Summary>Low (<a href="https://docs.oracle.com/javase/8/docs/api/java/util/Random.html#nextGaussian--">random.nextGaussian()</a> with a low bias)</Summary>
  
  ![Low 300]
</details>
<details>
  <Summary>Mid (<a href="https://docs.oracle.com/javase/8/docs/api/java/util/Random.html#nextGaussian--">random.nextGaussian()</a> with a mid bias)</Summary>
  
  ![Mid 300]
</details>
<details>
  <Summary>High (<a href="https://docs.oracle.com/javase/8/docs/api/java/util/Random.html#nextGaussian--">random.nextGaussian()</a> with a high bias)</Summary>
  
  ![High 300]
</details>
<details>
  <Summary><a href="https://en.wikipedia.org/wiki/Lehmer_random_number_generator">Park and Miller</a></Summary>
  
  Note that this algorithm is very slow, especially when you clamp it to very small values.
  ![PAM 300]
</details>
<details>
  <Summary><a href="https://en.wikipedia.org/wiki/Mersenne_Twister">Mersenne Twister</a></Summary>
  
  ![MT 300]
</details>
<details>
  <Summary>W (Mixture of Low, Mid, and High)</Summary>

  ```java
  private int generateW()
  {
      switch (Random.nextInt(3))
      {
          case 0:
              return Random.low(points.length / 2);
          case 1:
              return Random.mid(points.length / 3, points.length / 3 * 2);
          default:
              return Random.high(points.length / 2, points.length);
      }
  }
  ```
  ![W 300]
</details>
<details>
  <Summary>Humps (Two Mids side by side)</Summary>
  
  ```java
  private int generateHumps()
  {
      if (Random.nextInt(2) == 0)
          return Random.mid(points.length / 2);
      return Random.mid(points.length / 2, points.length);
  }
  ```
  ![Humps 300]
</details>
<details>
  <Summary>Valley (Mixture of Low, Mid-left, Mid-right, and High)</Summary>
  
  ```java
  private int generateValley()
  {
      switch (Random.nextInt(4))
      {
          case 0:
              return Random.low(points.length / 3);
          case 1:
              return Random.mid(points.length / 5 * 2);
          case 2:
              return Random.mid(points.length / 5 * 3, points.length);
          default:
              return Random.high(points.length / 3 * 2, points.length);
      }
  }  
  ```
  ![Valley 300]
</details>

[Standard 300]: https://i.imgur.com/gUHLzSA.png
[Standard 20]: https://i.imgur.com/A0iOCQb.png
[High 300]: https://i.imgur.com/wS7JbFX.png
[Humps 300]: https://i.imgur.com/erH59Vg.png
[Low 300]: https://i.imgur.com/WYpBuqb.png
[Mid 300]: https://i.imgur.com/BzS3RpE.png
[PAM 300]: https://i.imgur.com/XwsIaR0.png
[MT 300]: https://i.imgur.com/QHZGSLF.png
[Valley 300]: https://i.imgur.com/aqgnlyP.png
[W 300]: https://i.imgur.com/0UDsSNg.png
