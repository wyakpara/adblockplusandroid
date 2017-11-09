import shlex, subprocess, time

sites = [
    # 'adb logcat | grep -e \'TestTimer\' > ../out.txt',
    'http://www.espn.com',
    'http://us.blizzard.com',
    'http://www.nexusmods.com',
    'http://store.steampowered.com',
    'http://www.disney.com',
    'http://www.alexa.com',
    'http://www.audible.com',
    'http://www.thinkgeek.com',
    'http://www.creepypasta.com',
    'http://fandom.wikia.com',
    'http://tvtropes.org',
    'http://www.forever21.com',
    'http://www.gap.com',
    'http://www.goodhousekeeping.com',
    'http://www.foodnetwork.com',
    'http://www.zappos.com',
    'http://shop.lululemon.com',
    'http://www.muscleegg.com',
    'http://damndelicious.net',
    'http://allrecipes.com',
    'http://www.wikihow.com',
    'http://www.webmd.com',
    'http://www.bestbuy.com',
    'http://www.giantitp.com',
    'http://www.hbo.com',
    'http://www.cnn.com',
    'http://www.huffingtonpost.com',
    'http://www.politico.com',
    'http://www.petsmart.com',
    'http://www.firecracker.me',
    'http://www.moddb.com',
    'http://www.ign.com',
    'http://www.firaxis.com',
    'http://www.zales.com',
    'http://www.nationalgeographic.com',
    'http://www.safeway.com',
    'http://www.wholefoodsmarket.com',
    'http://www.consumerreports.com',
    'http://www.sears.com',
    'http://www.pcgamer.com',
    'http://www.techradar.com',
    'http://www.phoenix.edu',
    'http://redlettermedia.com',
    'http://roosterteeth.com',
    'http://www.dccomics.com',
    'http://marvel.com',
    'http://www.cracked.com',
    'http://www.chegg.com',
    'http://starwoodhotels.com',
    'http://katu.com',
    'http://instructables.com',
    'http://wtol.com'
]
commands = ['adb shell am start -a "android.intent.action.VIEW" -d "' + site + '"' for site in sites]

# Clear Chrome
split_command = shlex.split('adb shell pm clear com.android.chrome')
subprocess.call(split_command)
time.sleep(20)

for command in commands:
    split_command = shlex.split(command)
    print(split_command)
    subprocess.call(split_command)
    time.sleep(20)
split_command = shlex.split('killall adb')
subprocess.call(split_command)