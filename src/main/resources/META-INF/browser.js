var timers = [],
    window = {
        document: {
            getElementById: function(id) { 
                return [];
            },
            getElementsByTagName: function(tagName) {
                return [];
            }
        },
        location: {
            protocol: 'file:', 
            hostname: 'localhost', 
            port: '80'
        },
        setInterval: function(fn, time) {
            var num = timers.length;
            timers[num] = new java.lang.Thread(new java.lang.Runnable({
                run: function() {
                    while (true) {
                        java.lang.Thread.currentThread().sleep(time);
                        fn();
                    }
                }
            }));
            timers[num].start();
            return num;
        }
    },
    document = window.document,
    location = window.location,
    setInterval = window.setInterval;
