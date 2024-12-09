const https = require('https');

const getLeaderboardData = async () => {
  const options = {
    hostname: 'adventofcode.com',
    path: '/2024/leaderboard/private/view/156320.json',
    headers: {
      'Cookie': 'session=53616c7465645f5fe5ecf6e9f110062d560911b8a74d943fca4179b461253a1dba36776c541bed850db2acbe36b13d63ab8f410f525e7ee7ca7264b077ea83ec'
    }
  };

  const response = await new Promise((resolve, reject) => {
    https.get(options, (res) => {
      let data = '';

      res.on('data', (chunk) => {
        data += chunk;
      });

      res.on('end', () => {
        resolve({
          ok: res.statusCode === 200,
          status: res.statusCode,
          json: () => JSON.parse(data)
        });
      });
    }).on('error', (e) => {
      reject(e);
    });
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  return response.json();
};

const displayLeaderboard = async () => {
  try {
    const leaderboard = await getLeaderboardData();
    const participants = leaderboard.members;

    const sortedParticipants = Object.values(participants).sort((a, b) => {
      return Object.keys(b.completion_day_level).length - Object.keys(a.completion_day_level).length;
    });

    sortedParticipants.forEach(participant => {
      console.log(`Participant: ${participant.name}`);
      const completionDays = participant.completion_day_level;

      Object.keys(completionDays).forEach(day => {
        const dayLevels = completionDays[day];
        Object.keys(dayLevels).forEach(level => {
          const star = dayLevels[level];
          const date = new Date(star.get_star_ts * 1000);
          console.log(`  Day ${day}, Level ${level}: ${date.toLocaleString()}`);
        });
      });
    });
  } catch (error) {
    console.error(error);
  }
};

displayLeaderboard();
