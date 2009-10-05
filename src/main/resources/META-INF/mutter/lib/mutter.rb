#
# Mutter — the tiny command-line interface library with lots of style~
#

require 'yaml' 

$:.unshift File.dirname(__FILE__) + '/mutter'

require 'mutterer'
require 'indenter'
require 'ext'

module Mutter
  #
  # ANSI color & transform codes
  #
  #   If the value's an array, 
  #   [0] is the start code
  #   and [1] is the end code.
  #
  #   Colors all have the same
  #   reset code (39).
  #
  ANSI = {
    :reset => 0,
    :transforms => {
      :bold      => [1, 22],
      :underline => [4, 24],
      :blink     => [5, 25],
      :inverse   => [7, 27]
    },
    :colors => {
      :black  => 30, :red    => 31,
      :green  => 32, :yellow => 33,
      :blue   => 34, :purple => 35,
      :cyan   => 36, :white  => 37,
      :reset  => 39
    }
  }
  
  def self.indenter tab
    Indenter.new tab
  end
  
  def self.say *args
    new.say *args
  end
  
  def self.stylize *args
    new.stylize *args
  end
  
  def self.new *args
    Mutterer.new(*args)
  end
end